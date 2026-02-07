package com.clinic.medical_clinic_scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MedicalClinicSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicalClinicSchedulerApplication.class, args);
	}

}
