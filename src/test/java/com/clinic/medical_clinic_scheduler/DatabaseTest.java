package com.clinic.medical_clinic_scheduler;

import com.clinic.medical_clinic_scheduler.model.Patient;
import com.clinic.medical_clinic_scheduler.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
// @ActiveProfiles("test")
class DatabaseTest {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void shouldSaveAndRetrievePatient() {
        // given
        Patient patient = Patient.builder()
                .email("test@example.com")
                .password("secret123")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("123456789")
                .build();

        // when
        Patient savedPatient = patientRepository.save(patient);

        // then
        assertThat(savedPatient.getId()).isNotNull();
        assertThat(savedPatient.getEmail()).isEqualTo("test@example.com");

        boolean exists = patientRepository.existsByEmail("test@example.com");
        assertThat(exists).isTrue();
    }
}