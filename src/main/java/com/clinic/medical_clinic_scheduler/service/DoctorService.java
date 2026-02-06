package com.clinic.medical_clinic_scheduler.service;

import com.clinic.medical_clinic_scheduler.dto.DoctorCreateDTO;
import com.clinic.medical_clinic_scheduler.dto.DoctorDTO;
import java.util.List;

public interface DoctorService {
    DoctorDTO createDoctor(DoctorCreateDTO doctorCreateDTO);
    List<DoctorDTO> getAllDoctors();
    void deleteDoctor(Long id);
}