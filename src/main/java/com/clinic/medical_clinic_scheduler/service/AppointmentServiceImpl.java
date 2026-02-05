package com.clinic.medical_clinic_scheduler.service;

import com.clinic.medical_clinic_scheduler.dto.AppointmentDTO;
import com.clinic.medical_clinic_scheduler.dto.ScheduleRequestDTO;
import com.clinic.medical_clinic_scheduler.mapper.AppointmentMapper;
import com.clinic.medical_clinic_scheduler.model.Appointment;
import com.clinic.medical_clinic_scheduler.model.AppointmentStatus;
import com.clinic.medical_clinic_scheduler.model.Doctor;
import com.clinic.medical_clinic_scheduler.repository.AppointmentRepository;
import com.clinic.medical_clinic_scheduler.repository.DoctorRepository;
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
}