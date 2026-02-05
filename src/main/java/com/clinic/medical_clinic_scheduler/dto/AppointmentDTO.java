package com.clinic.medical_clinic_scheduler.dto;

import com.clinic.medical_clinic_scheduler.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
}