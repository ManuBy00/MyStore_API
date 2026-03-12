package com.mby.myStore.Services;

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
    public List<Cliente> getAll(){
        List<Cliente> clientes = clienteRepository.findAll();
        if (clientes.isEmpty()){
            return new ArrayList<Cliente>();
        }else {
            return clientes;
        }
    }

    /**
     * busca un cliente a partir de su id. Usa optional para gestionar nulos
     * @param id del cliente a buscar
     * @return el cliente con el id introducido
     */
    public Cliente getClienteById(int id){
        return clienteRepository.findById(id)
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
    public void updateCliente(Cliente clienteNuevo, int id){
        if (clienteNuevo!= null && clienteRepository.existsById(id)){
            Optional<Cliente> cliente = clienteRepository.findById(id);
            Cliente newCliente = cliente.get();
            newCliente.setEmail(clienteNuevo.getEmail());
            newCliente.setNombre(clienteNuevo.getNombre());
            newCliente.setPassword(clienteNuevo.getPassword());
            clienteRepository.save(newCliente);
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
     * Devuelve un cliente a partir de su email.
     * @param email
     * @return cliente encontrado
     */
    public Cliente getClienteByEmail(String email){
        return clienteRepository.getByEmail(email)
                .orElseThrow(()->new RecordNotFoundException("No existe un cliente con el email " + email));

    }

    public Cliente login(String email, String password) throws InvalidCredentialsException {
        Cliente cliente = getClienteByEmail(email);
        if (cliente == null){
            throw new InvalidCredentialsException("No existe un cliente con el email " + email);
        }

        if (HashPsw.checkPassword(password, cliente.getPassword())) {
            return cliente;
        } else {
            throw new InvalidCredentialsException("Contraseña incorrecta");
        }
    }

    public List<Cliente> getClientesByNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

}
