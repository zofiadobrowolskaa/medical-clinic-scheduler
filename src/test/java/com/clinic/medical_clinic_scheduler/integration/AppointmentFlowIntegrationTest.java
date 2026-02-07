package com.clinic.medical_clinic_scheduler.integration;

import com.clinic.medical_clinic_scheduler.dto.ScheduleRequestDTO;
import com.clinic.medical_clinic_scheduler.model.Appointment;
import com.clinic.medical_clinic_scheduler.model.Doctor;
import com.clinic.medical_clinic_scheduler.model.Role;
import com.clinic.medical_clinic_scheduler.repository.AppointmentRepository;
import com.clinic.medical_clinic_scheduler.repository.DoctorRepository;
import com.clinic.medical_clinic_scheduler.repository.PatientRepository;
import com.clinic.medical_clinic_scheduler.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AppointmentFlowIntegrationTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void shouldCreateScheduleInDatabase() {
        // 1. prepare data
        Doctor doctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .email("doc@test.com")
                .specialization("Cardiology")
                .password("pass")
                .role(Role.DOCTOR)
                .build();
        Doctor savedDoctor = doctorRepository.save(doctor);

        // 2. call service
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        LocalDateTime end = start.plusHours(1);

        ScheduleRequestDTO request = new ScheduleRequestDTO(
                savedDoctor.getId(),
                start,
                end,
                30
        );

        // 3. execute action
        appointmentService.createSchedule(request);

        List<Appointment> appointments = appointmentRepository.findAll();

        // 4. assertions
        assertThat(appointments).hasSize(2);
        assertThat(appointments.get(0).getDoctor().getEmail()).isEqualTo("doc@test.com");
    }

    @Test
    void shouldFailWhenTwoPatientsTryToBookSameAppointment() {
        // 1. setup data
        Doctor doctor = doctorRepository.save(Doctor.builder()
                .firstName("House").lastName("MD").email("house@md.com")
                .specialization("I").password("pass").role(Role.DOCTOR).build());

        com.clinic.medical_clinic_scheduler.model.Patient p1 = patientRepository.save(
                com.clinic.medical_clinic_scheduler.model.Patient.builder()
                        .firstName("P1").lastName("X").email("p1@test.com")
                        .password("pass").phoneNumber("111").role(Role.PATIENT).build());

        com.clinic.medical_clinic_scheduler.model.Patient p2 = patientRepository.save(
                com.clinic.medical_clinic_scheduler.model.Patient.builder()
                        .firstName("P2").lastName("Y").email("p2@test.com")
                        .password("pass").phoneNumber("222").role(Role.PATIENT).build());

        Appointment appointment = appointmentRepository.save(Appointment.builder()
                .doctor(doctor)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusMinutes(30))
                .status(com.clinic.medical_clinic_scheduler.model.AppointmentStatus.AVAILABLE)
                .build());

        // 2. simulate user 1 fetching data
        Appointment appointmentUser1 = appointmentRepository.findById(appointment.getId()).orElseThrow();

        // 3. user 1 books appointment (flush forces db update, version increments 0 -> 1)
        appointmentUser1.setStatus(com.clinic.medical_clinic_scheduler.model.AppointmentStatus.BOOKED);
        appointmentUser1.setPatient(p1);
        appointmentRepository.saveAndFlush(appointmentUser1);

        // 4. simulate user 2 loading data before user 1 finished
        // manually construct 'stale' object with old version (0) to simulate concurrency
        // (findById would return new version 1 from cache/db)
        Appointment appointmentUser2 = new Appointment();
        appointmentUser2.setId(appointment.getId());
        appointmentUser2.setVersion(0L); // force old version
        appointmentUser2.setDoctor(doctor);
        appointmentUser2.setStartTime(appointment.getStartTime());
        appointmentUser2.setEndTime(appointment.getEndTime());
        appointmentUser2.setStatus(com.clinic.medical_clinic_scheduler.model.AppointmentStatus.BOOKED);
        appointmentUser2.setPatient(p2);

        // 5. expect exception (user 2 sends version 0, db has version 1)
        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.orm.ObjectOptimisticLockingFailureException.class,
                () -> appointmentRepository.save(appointmentUser2)
        );
    }
}