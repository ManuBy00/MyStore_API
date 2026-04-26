package com.mby.myStore.Repositories;

import com.mby.myStore.Model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service,Integer> {

    Service getServiciosById(Integer id);
}
