package com.clinic.medical_clinic_scheduler.controller;

import com.clinic.medical_clinic_scheduler.dto.AppointmentDTO;
import com.clinic.medical_clinic_scheduler.dto.ScheduleRequestDTO;
import com.clinic.medical_clinic_scheduler.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/schedule")
    public ResponseEntity<List<AppointmentDTO>> createSchedule(@Valid @RequestBody ScheduleRequestDTO scheduleRequestDTO) {
        List<AppointmentDTO> createdAppointments = appointmentService.createSchedule(scheduleRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointments);
    }

    @PatchMapping("/{id}/book")
    public ResponseEntity<AppointmentDTO> bookAppointment(
            @PathVariable Long id,
            @Valid @RequestBody com.clinic.medical_clinic_scheduler.dto.BookAppointmentDTO bookAppointmentDTO) {

        AppointmentDTO bookedAppointment = appointmentService.bookAppointment(id, bookAppointmentDTO);
        return ResponseEntity.ok(bookedAppointment);
    }
}