package com.mby.myStore.Repositories;

import com.mby.myStore.Model.Role;
import com.mby.myStore.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Comprueba si existe un cliente con el email proporcionado.
     * Útil para validaciones de registro.
     */
    boolean existsByEmail(String email);

    /**
     * Busca un cliente por su email.
     * @return Un Optional que contiene al cliente si existe.
     */
    Optional<User> getByEmail(String email);

    List<User> findByNameContainingIgnoreCase(String nombre);

    List<User> findByRole(Role role);
}
