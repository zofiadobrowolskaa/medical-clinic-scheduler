package com.clinic.medical_clinic_scheduler.service;

import com.clinic.medical_clinic_scheduler.dto.DoctorCreateDTO;
import com.clinic.medical_clinic_scheduler.dto.DoctorDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {
    DoctorDTO createDoctor(DoctorCreateDTO doctorCreateDTO);
    Page<DoctorDTO> getAllDoctors(Pageable pageable);
    void deleteDoctor(Long id);
}