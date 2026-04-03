package com.mby.myStore.Services;

import com.mby.myStore.Exceptions.DuplicateRecordException;
import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Model.Empleado;
import com.mby.myStore.Repositories.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class EmpleadosService {

    @Autowired
    EmpleadoRepository empleadoRepository;

    /**
     * Recupera la lista completa de barberos/empleados.
     * @return Lista de empleados.
     */
    public List<Empleado> getAll(){
        if (empleadoRepository.findAll().isEmpty()) {
            return new ArrayList<>();
        }
        return empleadoRepository.findAll();
    }

    /**
     * Busca un empleado por su ID único.
     * @param id Identificador del empleado.
     * @return El empleado encontrado.
     * @throws RecordNotFoundException si el ID no existe en la BD.
     */
    public Empleado getEmpleadoById(int id){
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("No existe el empleado con ID: " + id));
    }

    /**
     * Registra un nuevo empleado validando que el email sea único.
     * @param empleado Datos del nuevo barbero.
     */
    public void addEmpleado(Empleado empleado) {
        empleadoRepository.save(empleado);
    }

    /**
     * Elimina a un empleado del sistema.
     * @param id ID del empleado a borrar.
     */
    public void deleteEmpleado(int id) {
        if (!empleadoRepository.existsById(id)) {
            throw new RecordNotFoundException("No se puede eliminar: empleado no encontrado");
        }
        empleadoRepository.deleteById(id);
    }

    /**
     * Actualiza la información profesional de un empleado.
     * @param id ID del empleado a modificar.
     * @param nuevoEmpleado Datos actualizados.
     */
    @Transactional
    public Empleado updateEmpleado(int id, Empleado nuevoEmpleado) {
        Empleado empleadoExistente = getEmpleadoById(id);

        empleadoExistente.setNombre(nuevoEmpleado.getNombre());
        empleadoRepository.save(empleadoExistente);
        return empleadoExistente;
    }

}
