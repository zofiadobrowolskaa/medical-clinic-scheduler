package com.clinic.medical_clinic_scheduler.repository;

import com.clinic.medical_clinic_scheduler.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByDoctorIdAndStartTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    List<Appointment> findAllByPatientId(Long patientId);

    boolean existsByDoctorId(Long doctorId);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.startTime < :endTime " +
            "AND a.endTime > :startTime")
    boolean existsOverlappingAppointments(@Param("doctorId") Long doctorId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);
}