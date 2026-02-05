package com.clinic.medical_clinic_scheduler.service;

import com.clinic.medical_clinic_scheduler.dto.DoctorCreateDTO;
import com.clinic.medical_clinic_scheduler.dto.DoctorDTO;
import com.clinic.medical_clinic_scheduler.exception.DoctorAlreadyExistsException;
import com.clinic.medical_clinic_scheduler.mapper.DoctorMapper;
import com.clinic.medical_clinic_scheduler.model.Doctor;
import com.clinic.medical_clinic_scheduler.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Override
    @Transactional
    public DoctorDTO createDoctor(DoctorCreateDTO doctorCreateDTO) {

        if (doctorRepository.existsByEmail(doctorCreateDTO.getEmail())) {
            throw new DoctorAlreadyExistsException("Doctor with email " + doctorCreateDTO.getEmail() + " already exists");
        }

        Doctor doctor = doctorMapper.toEntity(doctorCreateDTO);

        Doctor savedDoctor = doctorRepository.save(doctor);

        return doctorMapper.toDTO(savedDoctor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(doctorMapper::toDTO)
                .collect(Collectors.toList());
    }
}