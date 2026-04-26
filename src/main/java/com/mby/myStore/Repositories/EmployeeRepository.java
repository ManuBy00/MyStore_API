package com.mby.myStore.Repositories;

import com.mby.myStore.Model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Integer> {

    public List<Employee> findAllByActiveTrue();
    public List<Employee> findAllByActiveFalse();
    public int countByActiveTrue();



}
