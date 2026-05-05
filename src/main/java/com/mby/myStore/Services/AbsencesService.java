package com.mby.myStore.Services;

import com.mby.myStore.DTO.AbsenceRequest;
import com.mby.myStore.DTO.AbsenceResponse;
import com.mby.myStore.Exceptions.DateNotValidException;
import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Model.Absence;
import com.mby.myStore.Model.Employee;
import com.mby.myStore.Repositories.AbsenceRepository;
import com.mby.myStore.Repositories.AppointmentRepository;
import com.mby.myStore.Repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class AbsencesService {
    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public AbsenceResponse createAbsence(AbsenceRequest absenceRequest) {
        Employee employee = employeeRepository.findById(absenceRequest.getEmployeeId())
                .orElseThrow(() -> new RecordNotFoundException("Empleado no encontrado"));

        validateDates(absenceRequest.getStartDate(), absenceRequest.getEndDate());

        if (absenceRequest.getStartDate().isBefore(LocalDate.now())) {
            throw new DateNotValidException("La fecha de ausencia no puede ser de un día pasado");
        }

        Absence absenceEntity =  new Absence();
        absenceEntity.setEmployee(employee);
        absenceEntity.setStartDate(LocalDate.now());
        absenceEntity.setEndDate(LocalDate.now());
        absenceEntity.setReason(absenceRequest.getReason());

        absenceRepository.save(absenceEntity);
        return mapToResponse(absenceEntity);
    }

    public AbsenceResponse updateAbsence(int id, AbsenceRequest absenceDetails) {
        // 1. Verificar que la ausencia existe
        Absence existingAbsence = absenceRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Ausencia no encontrada con ID: " + id));

        // 2. Validar fechas nuevas
        validateDates(absenceDetails.getStartDate(), absenceDetails.getEndDate());

        // 3. Actualizar campos
        existingAbsence.setStartDate(absenceDetails.getStartDate());
        existingAbsence.setEndDate(absenceDetails.getEndDate());
        existingAbsence.setReason(absenceDetails.getReason());

        absenceRepository.save(existingAbsence);
        return mapToResponse(existingAbsence);
    }

    public void deleteAbsence(int id) {
        if (!absenceRepository.existsById(id)) {
            throw new RecordNotFoundException("No se puede borrar: Ausencia no encontrada");
        }
        absenceRepository.deleteById(id);
    }

    public List<AbsenceResponse> getAbsencesByEmployee(int employeeId) {
        if (employeeRepository.findById(employeeId).isEmpty()) {
            throw new RecordNotFoundException("Empleado no encontrado");
        }
        List<AbsenceResponse> absences = absenceRepository.getAbsenceByEmployeeId(employeeId).stream().map(this::mapToResponse).collect(Collectors.toList());
        return absences;
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new DateNotValidException("Las fechas no pueden ser nulas");
        }
        if (start.isAfter(end)) {
            throw new DateNotValidException("La fecha de inicio no puede ser posterior a la de fin");
        }

        if (start.isBefore(LocalDate.now())) {
            throw new DateNotValidException("La fecha de inicio no puede ser anterior a hoy");

        }
    }

    private AbsenceResponse mapToResponse(Absence absence) {
        AbsenceResponse response = new AbsenceResponse();

        // Mapeo de campos simples
        response.setId(absence.getId());
        response.setStartDate(absence.getStartDate());
        response.setEndDate(absence.getEndDate());
        response.setReason(absence.getReason());

        // Mapeo selectivo del empleado (Evita la recursión)
        if (absence.getEmployee() != null) {
            response.setEmployeeId(absence.getEmployee().getId());
            response.setEmployeeName(absence.getEmployee().getName());
        }

        return response;
    }


}
