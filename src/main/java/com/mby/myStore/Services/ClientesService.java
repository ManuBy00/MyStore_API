package com.mby.myStore.Services;

import com.mby.myStore.DTO.ClienteDTO;
import com.mby.myStore.Exceptions.DuplicateRecordException;
import com.mby.myStore.Exceptions.InvalidCredentialsException;
import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Model.Cliente;
import com.mby.myStore.Repositories.ClienteRepository;
import com.mby.myStore.Utils.HashPsw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientesService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Devuelve una lista de todos los clientes
     */
    public List<ClienteDTO> getAll(){
        List<ClienteDTO> clientes = clienteRepository.findAll().stream().map(this::entityToDTO).toList();
        if (clientes.isEmpty()) {
            return new ArrayList<>();
        }else {
            return clientes;
        }
    }

    /**
     * busca un cliente a partir de su id. Usa optional para gestionar nulos
     * @param id del cliente a buscar
     * @return el cliente con el id introducido
     */
    public ClienteDTO getClienteById(int id){
        return clienteRepository.findById(id)
                .map(this::entityToDTO)
                .orElseThrow(()->new RecordNotFoundException("No existe un cliente con el id " + id));
    }

    /**
     * añade un cliente a la base de datos después de  comprobar si su email ya está registrado
     * @param cliente a añadir
     */
    public void addCliente(Cliente cliente){
        if (clienteRepository.existsByEmail(cliente.getEmail())){
            throw new DuplicateRecordException("el email introducido ya existe " + cliente.getId());
        }
        clienteRepository.save(cliente);
    }

    /**
     * Actualiza los datos de un cliente, usa optional para controlar nulos
     * @param clienteNuevo
     * @param id
     */
    public ClienteDTO updateCliente(Cliente clienteNuevo, int id){
        if (clienteNuevo!= null && clienteRepository.existsById(id)){
            Optional<Cliente> cliente = clienteRepository.findById(id);
            Cliente newCliente = cliente.get();
            newCliente.setEmail(clienteNuevo.getEmail());
            newCliente.setNombre(clienteNuevo.getNombre());
            newCliente.setPassword(clienteNuevo.getPassword());
            clienteRepository.save(newCliente);

            return entityToDTO(newCliente);
        }else {
            throw new RecordNotFoundException("No existe un cliente con el id " + id);
        }
    }

    /**
     * elimina un  cliente por su id, comprobando primero si existe
     * @param id
     */
    public void deleteCliente(int id){
        if (clienteRepository.existsById(id)){
            clienteRepository.deleteById(id);
        }else {
            throw new RecordNotFoundException("No existe un cliente con el id " + id);
        }
    }


    /**
     * Devuelve un cliente a partir de su email. No se utilizamos DTO porque este es el método que usa el login.
     * @param email
     * @return cliente encontrado
     */
    public Cliente getClienteByEmail(String email){
        return clienteRepository.getByEmail(email)
                .orElseThrow(()->new RecordNotFoundException("No existe un cliente con el email " + email));

    }


    /**
     * Realiza la validación de acceso al sistema (Login).
     * Compara las credenciales introducidas con los datos almacenados en la base de datos.
     * * @param email Correo electrónico del usuario.
     *
     * @param password Contraseña en texto plano introducida en el formulario.
     * @return El objeto Cliente si la autenticación es exitosa.
     * @throws InvalidCredentialsException Si el usuario no existe o la contraseña no coincide.
     */
    public Cliente login(String email, String password) throws InvalidCredentialsException {
        Cliente cliente = getClienteByEmail(email);
        if (cliente == null) {
            throw new InvalidCredentialsException("No existe un cliente con el email " + email);
        }

        if (HashPsw.checkPassword(password, cliente.getPassword())) {
            return cliente;
        } else {
            throw new InvalidCredentialsException("Contraseña incorrecta");
        }
    }

    /**
     * Realiza una búsqueda flexible de clientes por su nombre.
     * @param nombre Cadena de texto a buscar.
     * @return Lista de clientes que contienen la cadena, sin distinguir mayúsculas de minúsculas.
     */
    public List<ClienteDTO> getClientesByNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre)
                .stream().map(this::entityToDTO).toList();
    }

    public ClienteDTO entityToDTO(Cliente cliente){
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(cliente.getId());
        clienteDTO.setEmail(cliente.getEmail());
        clienteDTO.setNombre(cliente.getNombre());
        clienteDTO.setFechaRegistro(cliente.getFechaRegistro());

        return clienteDTO;
    }

}
