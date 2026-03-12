package com.mby.myStore.Repositories;

import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    /**
     * Comprueba si existe un cliente con el email proporcionado.
     * Útil para validaciones de registro.
     */
    boolean existsByEmail(String email);

    /**
     * Busca un cliente por su email.
     * @return Un Optional que contiene al cliente si existe.
     */
    Optional<Cliente> getByEmail(String email);

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);




}
