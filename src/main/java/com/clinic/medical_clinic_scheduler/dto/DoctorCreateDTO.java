package com.clinic.medical_clinic_scheduler.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorCreateDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Specialization is required")
    @Size(min = 3, message = "Specialization must be at least 3 characters long")
    private String specialization;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}