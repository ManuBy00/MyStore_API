package com.mby.myStore.Repositories;

import com.mby.myStore.Model.BusinessShift;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface BusinessShiftRepository extends JpaRepository<BusinessShift, Integer> {
    // Obtener turnos de un día (ej: todos los Lunes) ordenados por hora de inicio
    List<BusinessShift> findByDayOfWeekOrderByStartTimeAsc(DayOfWeek dayOfWeek);

    // Útil para cuando el usuario guarde el nuevo horario: borramos lo viejo y metemos lo nuevo
    @Transactional
    void deleteByDayOfWeek(DayOfWeek dayOfWeek);
}
