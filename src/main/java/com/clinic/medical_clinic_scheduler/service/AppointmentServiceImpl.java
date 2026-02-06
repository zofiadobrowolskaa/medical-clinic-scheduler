package com.clinic.medical_clinic_scheduler.service;

import com.clinic.medical_clinic_scheduler.dto.AppointmentDTO;
import com.clinic.medical_clinic_scheduler.dto.BookAppointmentDTO;
import com.clinic.medical_clinic_scheduler.dto.ScheduleRequestDTO;
import com.clinic.medical_clinic_scheduler.mapper.AppointmentMapper;
import com.clinic.medical_clinic_scheduler.model.Appointment;
import com.clinic.medical_clinic_scheduler.model.AppointmentStatus;
import com.clinic.medical_clinic_scheduler.model.Doctor;
import com.clinic.medical_clinic_scheduler.repository.AppointmentRepository;
import com.clinic.medical_clinic_scheduler.repository.DoctorRepository;
import com.clinic.medical_clinic_scheduler.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    @Transactional
    public List<AppointmentDTO> createSchedule(ScheduleRequestDTO request) {

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        List<Appointment> newAppointments = new ArrayList<>();
        LocalDateTime currentSlotStart = request.getStartTime();

        while (currentSlotStart.plusMinutes(request.getSlotDurationMinutes()).isBefore(request.getEndTime()) ||
                currentSlotStart.plusMinutes(request.getSlotDurationMinutes()).isEqual(request.getEndTime())) {

            LocalDateTime currentSlotEnd = currentSlotStart.plusMinutes(request.getSlotDurationMinutes());

            Appointment appointment = Appointment.builder()
                    .startTime(currentSlotStart)
                    .endTime(currentSlotEnd)
                    .status(AppointmentStatus.AVAILABLE)
                    .doctor(doctor)
                    .patient(null)
                    .build();

            newAppointments.add(appointment);

            currentSlotStart = currentSlotEnd;
        }

        List<Appointment> savedAppointments = appointmentRepository.saveAll(newAppointments);

        return savedAppointments.stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentDTO bookAppointment(Long appointmentId, BookAppointmentDTO bookAppointmentDTO) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment with ID " + appointmentId + " not found"));

        if (appointment.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot book an appointment in the past");
        }

        if (appointment.getStatus() != AppointmentStatus.AVAILABLE) {
            throw new IllegalStateException("Appointment is already booked or cancelled");
        }

        com.clinic.medical_clinic_scheduler.model.Patient patient = patientRepository.findById(bookAppointmentDTO.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient with ID " + bookAppointmentDTO.getPatientId() + " not found"));

        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.BOOKED);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toDTO(updatedAppointment);
    }
}