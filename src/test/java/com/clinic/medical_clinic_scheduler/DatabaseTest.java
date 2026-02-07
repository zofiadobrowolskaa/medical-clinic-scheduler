package com.clinic.medical_clinic_scheduler;

import com.clinic.medical_clinic_scheduler.model.Patient;
import com.clinic.medical_clinic_scheduler.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // rollback changes after each test
@ActiveProfiles("test")
class DatabaseTest {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void shouldSaveAndRetrievePatient() {
        // given
        Patient patient = Patient.builder()
                .email("test-db@example.com")
                .password("secret123")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("123456789")
                .role("PATIENT")
                .build();

        // when
        Patient savedPatient = patientRepository.save(patient);

        // then
        assertThat(savedPatient.getId()).isNotNull();
        assertThat(savedPatient.getEmail()).isEqualTo("test-db@example.com");

        boolean exists = patientRepository.existsByEmail("test-db@example.com");
        assertThat(exists).isTrue();
    }
}