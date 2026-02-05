package com.clinic.medical_clinic_scheduler.service;

import com.clinic.medical_clinic_scheduler.dto.PatientCreateDTO;
import com.clinic.medical_clinic_scheduler.dto.PatientDTO;
import com.clinic.medical_clinic_scheduler.exception.PatientAlreadyExistsException;
import com.clinic.medical_clinic_scheduler.mapper.PatientMapper;
import com.clinic.medical_clinic_scheduler.model.Patient;
import com.clinic.medical_clinic_scheduler.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    @Transactional
    public PatientDTO createPatient(PatientCreateDTO patientCreateDTO) {

        if (patientRepository.existsByEmail(patientCreateDTO.getEmail())) {
            throw new PatientAlreadyExistsException("Patient with email " + patientCreateDTO.getEmail() + " already exists");
        }

        Patient patient = patientMapper.toEntity(patientCreateDTO);

        Patient savedPatient = patientRepository.save(patient);

        return patientMapper.toDTO(savedPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(patientMapper::toDTO)
                .collect(Collectors.toList());
    }
}