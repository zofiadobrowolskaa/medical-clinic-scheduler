package com.clinic.medical_clinic_scheduler.mapper;

import com.clinic.medical_clinic_scheduler.dto.PatientCreateDTO;
import com.clinic.medical_clinic_scheduler.dto.PatientDTO;
import com.clinic.medical_clinic_scheduler.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientDTO toDTO(Patient patient);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Patient toEntity(PatientCreateDTO patientCreateDTO);
}