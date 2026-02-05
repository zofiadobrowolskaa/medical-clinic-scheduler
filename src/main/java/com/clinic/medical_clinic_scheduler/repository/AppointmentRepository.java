package com.clinic.medical_clinic_scheduler.repository;

import com.clinic.medical_clinic_scheduler.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}