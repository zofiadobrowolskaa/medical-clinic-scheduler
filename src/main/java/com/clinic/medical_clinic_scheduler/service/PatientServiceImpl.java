package com.clinic.medical_clinic_scheduler.service;

import com.clinic.medical_clinic_scheduler.dto.PatientCreateDTO;
import com.clinic.medical_clinic_scheduler.dto.PatientDTO;
import com.clinic.medical_clinic_scheduler.exception.ActionNotAllowedException;
import com.clinic.medical_clinic_scheduler.exception.PatientAlreadyExistsException;
import com.clinic.medical_clinic_scheduler.exception.ResourceNotFoundException;
import com.clinic.medical_clinic_scheduler.mapper.PatientMapper;
import com.clinic.medical_clinic_scheduler.model.Appointment;
import com.clinic.medical_clinic_scheduler.model.Patient;
import com.clinic.medical_clinic_scheduler.model.Role;
import com.clinic.medical_clinic_scheduler.repository.AppointmentRepository;
import com.clinic.medical_clinic_scheduler.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public PatientDTO createPatient(PatientCreateDTO patientCreateDTO) {
        log.info("Creating new patient with email: {}", patientCreateDTO.getEmail());

        if (patientRepository.existsByEmail(patientCreateDTO.getEmail())) {
            throw new PatientAlreadyExistsException("Patient with email " + patientCreateDTO.getEmail() + " already exists");
        }

        Patient patient = patientMapper.toEntity(patientCreateDTO);

        patient.setPassword(passwordEncoder.encode(patientCreateDTO.getPassword()));
        patient.setRole(Role.PATIENT);

        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient created successfully with ID: {}", savedPatient.getId());

        return patientMapper.toDTO(savedPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientDTO> getAllPatients(Pageable pageable) {
        log.debug("Fetching patients page: {}", pageable);
        return patientRepository.findAll(pageable)
                .map(patientMapper::toDTO);
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        log.info("Attempting to delete patient with ID: {}", id);

        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient with ID " + id + " not found");
        }

        List<Appointment> appointments = appointmentRepository.findAllByPatientId(id);
        if (!appointments.isEmpty()) {
            throw new ActionNotAllowedException("Cannot delete patient who has scheduled or past appointments.");
        }

        patientRepository.deleteById(id);
        log.info("Patient with ID: {} deleted successfully", id);
    }
}