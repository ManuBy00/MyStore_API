package com.mby.myStore.Repositories;

import com.mby.myStore.Model.Absence;
import com.mby.myStore.Model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Integer> {

    List<Absence> getAbsenceByEmployeeId(int employeeId);

    @Query("SELECT COUNT(a) > 0 " +
            "FROM Absence a " +
            "WHERE a.employee.id = :employeeId " +
            "AND :date BETWEEN a.startDate AND a.endDate")
    boolean isEmployeeOnLeave(int employeeId, LocalDate date);


}