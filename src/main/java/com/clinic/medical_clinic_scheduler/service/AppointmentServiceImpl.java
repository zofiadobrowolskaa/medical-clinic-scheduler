package com.clinic.medical_clinic_scheduler.service;

import com.clinic.medical_clinic_scheduler.dto.AppointmentDTO;
import com.clinic.medical_clinic_scheduler.dto.BookAppointmentDTO;
import com.clinic.medical_clinic_scheduler.dto.ScheduleRequestDTO;
import com.clinic.medical_clinic_scheduler.exception.ActionNotAllowedException;
import com.clinic.medical_clinic_scheduler.exception.AppointmentConflictException;
import com.clinic.medical_clinic_scheduler.exception.ResourceNotFoundException;
import com.clinic.medical_clinic_scheduler.mapper.AppointmentMapper;
import com.clinic.medical_clinic_scheduler.model.Appointment;
import com.clinic.medical_clinic_scheduler.model.AppointmentStatus;
import com.clinic.medical_clinic_scheduler.model.Doctor;
import com.clinic.medical_clinic_scheduler.repository.AppointmentRepository;
import com.clinic.medical_clinic_scheduler.repository.DoctorRepository;
import com.clinic.medical_clinic_scheduler.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public List<AppointmentDTO> createSchedule(ScheduleRequestDTO request) {
        log.info("Creating schedule for doctor ID: {} from {} to {}", request.getDoctorId(), request.getStartTime(), request.getEndTime());

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with ID " + request.getDoctorId() + " not found"));

        boolean overlapExists = appointmentRepository.existsOverlappingAppointments(
                request.getDoctorId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (overlapExists) {
            throw new AppointmentConflictException("Doctor already has appointments scheduled in this time range.");
        }

        List<Appointment> newAppointments = new ArrayList<>();
        LocalDateTime currentSlotStart = request.getStartTime();

        while (currentSlotStart.plusMinutes(request.getSlotDuration()).isBefore(request.getEndTime()) ||
                currentSlotStart.plusMinutes(request.getSlotDuration()).isEqual(request.getEndTime())) {

            LocalDateTime currentSlotEnd = currentSlotStart.plusMinutes(request.getSlotDuration());

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
        log.info("Successfully created {} appointment slots", savedAppointments.size());

        return savedAppointments.stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentDTO bookAppointment(Long appointmentId, BookAppointmentDTO bookAppointmentDTO) {
        log.info("Patient ID: {} is attempting to book appointment ID: {}", bookAppointmentDTO.getPatientId(), appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment with ID " + appointmentId + " not found"));

        if (appointment.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ActionNotAllowedException("Cannot book an appointment in the past");
        }

        if (appointment.getStatus() != AppointmentStatus.AVAILABLE) {
            throw new AppointmentConflictException("Appointment is already booked or cancelled");
        }

        com.clinic.medical_clinic_scheduler.model.Patient patient = patientRepository.findById(bookAppointmentDTO.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient with ID " + bookAppointmentDTO.getPatientId() + " not found"));

        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.BOOKED);

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Successfully booked appointment ID: {}", appointmentId);

        String emailContent = "Your appointment with Dr. " + updatedAppointment.getDoctor().getLastName() +
                " is confirmed for " + updatedAppointment.getStartTime();

        notificationService.sendAppointmentConfirmation(
                patient.getEmail(),
                "Appointment Confirmation",
                emailContent
        );

        return appointmentMapper.toDTO(updatedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAvailableSlots(Long doctorId, String date) {
        log.debug("Fetching available slots for doctor ID: {} on date: {}", doctorId, date);
        LocalDate searchDate = LocalDate.parse(date);
        LocalDateTime startOfDay = searchDate.atStartOfDay();
        LocalDateTime endOfDay = searchDate.atTime(LocalTime.MAX);

        List<Appointment> appointments = appointmentRepository.findAllByDoctorIdAndStartTimeBetween(doctorId, startOfDay, endOfDay);

        return appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.AVAILABLE)
                .filter(a -> a.getStartTime().isAfter(LocalDateTime.now()))
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getPatientAppointments(Long patientId) {
        log.debug("Fetching appointments for patient ID: {}", patientId);
        return appointmentRepository.findAllByPatientId(patientId)
                .stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentDTO cancelAppointment(Long appointmentId) {
        log.info("Attempting to cancel appointment ID: {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment with ID " + appointmentId + " not found"));

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new ActionNotAllowedException("Only booked appointments can be cancelled");
        }

        if (appointment.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ActionNotAllowedException("Cannot cancel an appointment that has already taken place");
        }

        appointment.setPatient(null);
        appointment.setStatus(AppointmentStatus.AVAILABLE);

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Successfully cancelled appointment ID: {}", appointmentId);

        return appointmentMapper.toDTO(saved);
    }
}