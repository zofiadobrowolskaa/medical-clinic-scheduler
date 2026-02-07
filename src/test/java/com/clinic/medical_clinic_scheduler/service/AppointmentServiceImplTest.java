package com.clinic.medical_clinic_scheduler.service;

import com.clinic.medical_clinic_scheduler.dto.AppointmentDTO;
import com.clinic.medical_clinic_scheduler.dto.BookAppointmentDTO;
import com.clinic.medical_clinic_scheduler.mapper.AppointmentMapper;
import com.clinic.medical_clinic_scheduler.model.Appointment;
import com.clinic.medical_clinic_scheduler.model.AppointmentStatus;
import com.clinic.medical_clinic_scheduler.model.Doctor;
import com.clinic.medical_clinic_scheduler.model.Patient;
import com.clinic.medical_clinic_scheduler.repository.AppointmentRepository;
import com.clinic.medical_clinic_scheduler.repository.DoctorRepository;
import com.clinic.medical_clinic_scheduler.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    void shouldBookAppointmentSuccessfully() {
        // given
        Long appointmentId = 100L;
        Long patientId = 1L;
        BookAppointmentDTO request = new BookAppointmentDTO(patientId);

        Patient patient = Patient.builder().id(patientId).email("jan@test.com").build();
        Appointment appointment = Appointment.builder()
                .id(appointmentId)
                .status(AppointmentStatus.AVAILABLE)
                .startTime(LocalDateTime.now().plusDays(1))
                .doctor(Doctor.builder().firstName("House").lastName("MD").build()) // required for mapper logic
                .build();

        // mock repository behavior
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArguments()[0]);

        // mock mapper behavior
        when(appointmentMapper.toDTO(any())).thenReturn(AppointmentDTO.builder().status(AppointmentStatus.BOOKED).build());

        // when
        AppointmentDTO result = appointmentService.bookAppointment(appointmentId, request);

        // then
        verify(appointmentRepository).save(appointment);
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.BOOKED);
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.BOOKED);
        assertThat(appointment.getPatient()).isEqualTo(patient);
    }

    @Test
    void shouldThrowExceptionWhenBookingInPast() {
        // given
        Long appointmentId = 100L;
        Appointment appointment = Appointment.builder()
                .id(appointmentId)
                .status(AppointmentStatus.AVAILABLE)
                .startTime(LocalDateTime.now().minusDays(1))
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // when & then
        assertThrows(IllegalStateException.class, () ->
                appointmentService.bookAppointment(appointmentId, new BookAppointmentDTO(1L))
        );
    }
}