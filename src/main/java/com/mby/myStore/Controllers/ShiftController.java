package com.mby.myStore.Controllers;

import com.mby.myStore.Model.BusinessShift;
import com.mby.myStore.Repositories.BusinessShiftRepository;
import com.mby.myStore.Services.ShiftService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shifts")
public class ShiftController {

    @Autowired
    private ShiftService service;

    @PutMapping("/all")
    @Transactional
    public ResponseEntity<?> updateAllDaysHours(@RequestBody Map<DayOfWeek, List<BusinessShift>> fullSchedule) {
        service.updateAllSchedule(fullSchedule);
        return ResponseEntity.ok().build();
    }
}