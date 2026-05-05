package com.mby.myStore.Services;

import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Model.Employee;
import com.mby.myStore.Repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    /**
     * Recupera la lista completa de barberos/empleados.
     * @return Lista de empleados.
     */
    public List<Employee> getAll(){
        if (employeeRepository.findAll().isEmpty()) {
            return new ArrayList<>();
        }
        return employeeRepository.findAll();
    }

    /**
     * Busca un empleado por su ID único.
     * @param id Identificador del empleado.
     * @return El empleado encontrado.
     * @throws RecordNotFoundException si el ID no existe en la BD.
     */
    public Employee getEmployeeById(int id){
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("No existe el empleado con ID: " + id));
    }

    /**
     * Registra un nuevo empleado validando que el email sea único.
     * @param employee Datos del nuevo barbero.
     */
    public void addEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    /**
     * Elimina a un empleado del sistema.
     * @param id ID del empleado a borrar.
     */
    public void deleteEmployee(int id) {
        if (!employeeRepository.existsById(id)) {
            throw new RecordNotFoundException("No se puede eliminar: empleado no encontrado");
        }
        employeeRepository.deleteById(id);
    }

    /**
     * Actualiza la información profesional de un empleado.
     * @param id ID del empleado a modificar.
     * @param nuevoEmployee Datos actualizados.
     */
    @Transactional
    public Employee updateEmployee(int id, Employee nuevoEmployee) {
        Employee employeeExistente = getEmployeeById(id);

        employeeExistente.setName(nuevoEmployee.getName());
        employeeExistente.setActive(nuevoEmployee.getActive());
        employeeExistente.setHireDate(nuevoEmployee.getHireDate());
        employeeRepository.save(employeeExistente);
        return employeeExistente;
    }

    public int getActiveEmployeesCount() {
        // Esto llama a employeeRepository.countByActiveTrue()
        return employeeRepository.countByActiveTrue();
    }




}
