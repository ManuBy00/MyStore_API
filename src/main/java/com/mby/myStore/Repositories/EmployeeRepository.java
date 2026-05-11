package com.mby.myStore.Repositories;

import com.mby.myStore.Model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

     List<Employee> findAllByActiveTrue();
     List<Employee> findAllByActiveFalse();
     Long countByActiveTrue();



}
