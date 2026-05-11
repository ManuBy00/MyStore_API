package com.mby.myStore.Repositories;

import com.mby.myStore.Model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service,Long> {



    Optional<Service> getServiciosById(Integer id);
}
