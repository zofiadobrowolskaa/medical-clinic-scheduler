package com.clinic.medical_clinic_scheduler.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDTO {

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Start time is required")
    @FutureOrPresent(message = "Start time must be in the future or present")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @FutureOrPresent(message = "End time must be in the future or present")
    private LocalDateTime endTime;

    @NotNull(message = "Slot duration is required")
    private Integer slotDurationMinutes;
}