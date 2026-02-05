package com.clinic.medical_clinic_scheduler.mapper;

import com.clinic.medical_clinic_scheduler.dto.AppointmentDTO;
import com.clinic.medical_clinic_scheduler.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(expression = "java(appointment.getDoctor().getFirstName() + \" \" + appointment.getDoctor().getLastName())", target = "doctorName")
    AppointmentDTO toDTO(Appointment appointment);
}