package com.clinic.medical_clinic_scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest

// activates 'test' profile (application-test.yaml) using H2 database
@ActiveProfiles("test")
class MedicalClinicSchedulerApplicationTests {

	@Test
	void contextLoads() {
		// sanity check to ensure the Spring application context loads successfully
	}

}