package com.clinic.medical_clinic_scheduler.mapper;

import com.clinic.medical_clinic_scheduler.dto.DoctorCreateDTO;
import com.clinic.medical_clinic_scheduler.dto.DoctorDTO;
import com.clinic.medical_clinic_scheduler.model.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorDTO toDTO(Doctor doctor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "role", ignore = true)
    Doctor toEntity(DoctorCreateDTO doctorCreateDTO);
}