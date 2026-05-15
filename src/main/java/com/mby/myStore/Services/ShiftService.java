package com.mby.myStore.Services;

import com.mby.myStore.Exceptions.DateNotValidException;
import com.mby.myStore.Model.BusinessShift;
import com.mby.myStore.Repositories.BusinessShiftRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ShiftService {
    @Autowired
    private BusinessShiftRepository businessShiftRepository;

    private BusinessShift saveShift(BusinessShift businessShift){
        return businessShiftRepository.save(businessShift);
    }

    private void deleteShiftByDay(DayOfWeek day) {
        businessShiftRepository.deleteByDayOfWeek(day);
    }

    @Transactional
    public void updateAllSchedule(Map<DayOfWeek, List<BusinessShift>> fullSchedule) {
        // 1. Validar reglas de negocio
        validateFullSchedule(fullSchedule);

        // 2. Si pasa la validación, procedemos
        businessShiftRepository.deleteAll();

        fullSchedule.forEach((day, shifts) -> {
            if (shifts != null && !shifts.isEmpty()) {
                shifts.forEach(shift -> shift.setDayOfWeek(day));
                businessShiftRepository.saveAll(shifts);
            }
        });
    }

    private void validateFullSchedule(Map<DayOfWeek, List<BusinessShift>> fullSchedule) {
        fullSchedule.forEach((day, shifts) -> {
            if (shifts == null || shifts.size() < 2) {
                // Si hay un solo turno, solo validamos que inicio < fin
                if (shifts != null && !shifts.isEmpty()) {
                    validateShift(shifts.get(0), day);
                }
                return;
            }

            // Ordenar turnos por hora de inicio
            shifts.sort(Comparator.comparing(BusinessShift::getStartTime));

            for (int i = 0; i < shifts.size() - 1; i++) {
                BusinessShift current = shifts.get(i);
                BusinessShift next = shifts.get(i + 1);

                validateShift(current, day);

                // Validar solapamiento: ¿El siguiente empieza antes de que termine el actual?
                if (next.getStartTime().isBefore(current.getEndTime())) {
                    throw new DateNotValidException(
                            "Error en " + day + ": El turno de tarde (" + next.getStartTime() +
                                    ") no puede empezar antes de que termine el de mañana (" + current.getEndTime() + ")."
                    );
                }
            }
            // Validar el último turno de la lista (que inicio < fin)
            validateShift(shifts.get(shifts.size()-1), day);
        });
    }

    private void validateShift(BusinessShift shift, DayOfWeek day) {
        if (!shift.getStartTime().isBefore(shift.getEndTime())) {
            throw new DateNotValidException(
                    "Error en " + day + ": La hora de inicio (" + shift.getStartTime() +
                            ") debe ser anterior a la de fin (" + shift.getEndTime() + ")."
            );
        }
    }

}
