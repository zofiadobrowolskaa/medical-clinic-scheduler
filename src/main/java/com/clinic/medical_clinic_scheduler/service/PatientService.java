package com.clinic.medical_clinic_scheduler.service;

import com.clinic.medical_clinic_scheduler.dto.PatientCreateDTO;
import com.clinic.medical_clinic_scheduler.dto.PatientDTO;

import java.util.List;

public interface PatientService {
    PatientDTO createPatient(PatientCreateDTO patientCreateDTO);
    List<PatientDTO> getAllPatients();

    void deletePatient(Long id);
}