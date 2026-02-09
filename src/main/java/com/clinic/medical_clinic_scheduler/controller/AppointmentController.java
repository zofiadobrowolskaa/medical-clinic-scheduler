package com.clinic.medical_clinic_scheduler.controller;

import com.clinic.medical_clinic_scheduler.dto.AppointmentDTO;
import com.clinic.medical_clinic_scheduler.dto.ScheduleRequestDTO;
import com.clinic.medical_clinic_scheduler.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/schedule")
    public ResponseEntity<List<AppointmentDTO>> createSchedule(
            @jakarta.validation.Valid @RequestBody ScheduleRequestDTO scheduleRequestDTO) {
        return ResponseEntity.ok(appointmentService.createSchedule(scheduleRequestDTO));
    }

    @PatchMapping("/{id}/book")
    public ResponseEntity<AppointmentDTO> bookAppointment(
            @PathVariable Long id,
            @Valid @RequestBody com.clinic.medical_clinic_scheduler.dto.BookAppointmentDTO bookAppointmentDTO) {

        AppointmentDTO bookedAppointment = appointmentService.bookAppointment(id, bookAppointmentDTO);
        return ResponseEntity.ok(bookedAppointment);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AppointmentDTO>> getAvailableAppointments(
            @RequestParam Long doctorId,
            @RequestParam String date) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(doctorId, date));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> getPatientAppointments(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patientId));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentDTO> cancelAppointment(@PathVariable Long id) {
        AppointmentDTO cancelledAppointment = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(cancelledAppointment);
    }
}