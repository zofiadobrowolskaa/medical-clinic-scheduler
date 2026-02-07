package com.clinic.medical_clinic_scheduler.service;

import com.clinic.medical_clinic_scheduler.dto.PatientCreateDTO;
import com.clinic.medical_clinic_scheduler.dto.PatientDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {
    PatientDTO createPatient(PatientCreateDTO patientCreateDTO);
    Page<PatientDTO> getAllPatients(Pageable pageable);

    void deletePatient(Long id);
}