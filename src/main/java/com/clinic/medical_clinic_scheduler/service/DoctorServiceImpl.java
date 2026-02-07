package com.clinic.medical_clinic_scheduler.service;

import com.clinic.medical_clinic_scheduler.dto.DoctorCreateDTO;
import com.clinic.medical_clinic_scheduler.dto.DoctorDTO;
import com.clinic.medical_clinic_scheduler.exception.ActionNotAllowedException;
import com.clinic.medical_clinic_scheduler.exception.DoctorAlreadyExistsException;
import com.clinic.medical_clinic_scheduler.exception.ResourceNotFoundException;
import com.clinic.medical_clinic_scheduler.mapper.DoctorMapper;
import com.clinic.medical_clinic_scheduler.model.Doctor;
import com.clinic.medical_clinic_scheduler.model.Role;
import com.clinic.medical_clinic_scheduler.repository.AppointmentRepository;
import com.clinic.medical_clinic_scheduler.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public DoctorDTO createDoctor(DoctorCreateDTO doctorCreateDTO) {
        log.info("Creating new doctor with email: {}", doctorCreateDTO.getEmail());

        if (doctorRepository.existsByEmail(doctorCreateDTO.getEmail())) {
            throw new DoctorAlreadyExistsException("Doctor with email " + doctorCreateDTO.getEmail() + " already exists");
        }

        Doctor doctor = doctorMapper.toEntity(doctorCreateDTO);

        doctor.setPassword(passwordEncoder.encode(doctorCreateDTO.getPassword()));
        doctor.setRole(Role.DOCTOR);

        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Doctor created successfully with ID: {}", savedDoctor.getId());

        return doctorMapper.toDTO(savedDoctor);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorDTO> getAllDoctors(Pageable pageable) {
        log.debug("Fetching doctors page: {}", pageable);
        return doctorRepository.findAll(pageable)
                .map(doctorMapper::toDTO);
    }

    @Override
    @Transactional
    public void deleteDoctor(Long id) {
        log.info("Attempting to delete doctor with ID: {}", id);

        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Doctor with ID " + id + " not found");
        }

        if (appointmentRepository.existsByDoctorId(id)) {
            throw new ActionNotAllowedException("Cannot delete doctor who has assigned appointments.");
        }

        doctorRepository.deleteById(id);
        log.info("Doctor with ID: {} deleted successfully", id);
    }
}