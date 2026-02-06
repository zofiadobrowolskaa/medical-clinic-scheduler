package com.clinic.medical_clinic_scheduler.service;

import com.clinic.medical_clinic_scheduler.dto.PatientCreateDTO;
import com.clinic.medical_clinic_scheduler.dto.PatientDTO;
import com.clinic.medical_clinic_scheduler.exception.PatientAlreadyExistsException;
import com.clinic.medical_clinic_scheduler.mapper.PatientMapper;
import com.clinic.medical_clinic_scheduler.model.Appointment;
import com.clinic.medical_clinic_scheduler.model.Patient;
import com.clinic.medical_clinic_scheduler.repository.AppointmentRepository;
import com.clinic.medical_clinic_scheduler.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public PatientDTO createPatient(PatientCreateDTO patientCreateDTO) {

        if (patientRepository.existsByEmail(patientCreateDTO.getEmail())) {
            throw new PatientAlreadyExistsException("Patient with email " + patientCreateDTO.getEmail() + " already exists");
        }

        Patient patient = patientMapper.toEntity(patientCreateDTO);

        patient.setPassword(passwordEncoder.encode(patientCreateDTO.getPassword()));
        patient.setRole("PATIENT");

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

    @Override
    @Transactional
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new EntityNotFoundException("Patient with ID " + id + " not found");
        }

        List<Appointment> appointments = appointmentRepository.findAllByPatientId(id);
        if (!appointments.isEmpty()) {
            throw new IllegalStateException("Cannot delete patient who has scheduled or past appointments.");
        }

        patientRepository.deleteById(id);
    }
}