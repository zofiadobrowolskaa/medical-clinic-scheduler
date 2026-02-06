package com.clinic.medical_clinic_scheduler.controller;

import com.clinic.medical_clinic_scheduler.dto.DoctorCreateDTO;
import com.clinic.medical_clinic_scheduler.dto.DoctorDTO;
import com.clinic.medical_clinic_scheduler.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorDTO> createDoctor(@Valid @RequestBody DoctorCreateDTO doctorCreateDTO) {
        DoctorDTO createdDoctor = doctorService.createDoctor(doctorCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDoctor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }
}