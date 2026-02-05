package com.clinic.medical_clinic_scheduler.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookAppointmentDTO {

    @NotNull(message = "Patient ID is required")
    private Long patientId;
}