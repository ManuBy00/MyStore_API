package com.mby.myStore.Services;

import com.mby.myStore.DTO.UserDTO;
import com.mby.myStore.Exceptions.DuplicateRecordException;
import com.mby.myStore.Exceptions.InvalidCredentialsException;
import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Model.Role;
import com.mby.myStore.Model.User;
import com.mby.myStore.Repositories.UserRepository;
import com.mby.myStore.Utils.HashPsw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Devuelve una lista de todos los clientes
     */
    public List<UserDTO> getAll(){
        List<UserDTO> users = userRepository.findAll().stream().map(this::entityToDTO).toList();
        if (users.isEmpty()) {
            return new ArrayList<>();
        }else {
            return users;
        }
    }

    /**
     * busca un cliente a partir de su id. Usa optional para gestionar nulos
     * @param id del cliente a buscar
     * @return el cliente con el id introducido
     */
    public UserDTO getUserById(int id){
        return userRepository.findById(id)
                .map(this::entityToDTO)
                .orElseThrow(()->new RecordNotFoundException("No existe un cliente con el id " + id));
    }

    /**
     * añade un cliente a la base de datos después de  comprobar si su email ya está registrado
     * @param user a añadir
     */
    public void addUser(User user){
        if (userRepository.existsByEmail(user.getEmail())){
            throw new DuplicateRecordException("el email introducido ya existe " + user.getId());
        }
        userRepository.save(user);
    }

    /**
     * Actualiza los datos de un cliente, usa optional para controlar nulos
     * @param userNuevo
     * @param id
     */
    public UserDTO updateUser(User userNuevo, int id){
        if (userNuevo != null && userRepository.existsById(id)){
            Optional<User> cliente = userRepository.findById(id);
            User newUser = cliente.get();
            newUser.setEmail(userNuevo.getEmail());
            newUser.setName(userNuevo.getName());
            newUser.setPassword(userNuevo.getPassword());
            newUser.setTelNumber(cliente.get().getTelNumber());
            userRepository.save(newUser);

            return entityToDTO(newUser);
        }else {
            throw new RecordNotFoundException("No existe un usuario con el id " + id);
        }
    }

    /**
     * elimina un  usuario por su id, comprobando primero si existe
     * @param id
     */
    public void deleteUser(int id){
        if (userRepository.existsById(id)){
            userRepository.deleteById(id);
        }else {
            throw new RecordNotFoundException("No existe un cliente con el id " + id);
        }
    }


    /**
     * Devuelve un cliente a partir de su email. No se utilizamos DTO porque este es el método que usa el login.
     * @param email
     * @return cliente encontrado
     */
    public User getUserByEmail(String email){
        return userRepository.getByEmail(email)
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
    public UserDTO login(String email, String password) throws InvalidCredentialsException {
        User user = getUserByEmail(email);
        if (user == null) {
            throw new RecordNotFoundException("No existe un cliente con el email " + email);
        }

        if (HashPsw.checkPassword(password, user.getPassword())) {
            return entityToDTO(user);
        } else {
            throw new InvalidCredentialsException("Contraseña incorrecta");
        }
    }

    /**
     * Realiza una búsqueda flexible de clientes por su nombre.
     * @param nombre Cadena de texto a buscar.
     * @return Lista de clientes que contienen la cadena, sin distinguir mayúsculas de minúsculas.
     */
    public List<UserDTO> getUsersByName(String nombre) {
        return userRepository.findByNameContainingIgnoreCase(nombre)
                .stream().map(this::entityToDTO).toList();
    }

    public List<UserDTO> getUsersByRol(Role rol) {
        return  userRepository.findByRole(rol)
                .stream().map(this::entityToDTO).toList();
    }

    public UserDTO entityToDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setTelNumber(user.getTelNumber());
        userDTO.setRegisterDate(user.getRegisterDate());
        userDTO.setRole(user.getRole());

        return userDTO;
    }

}
