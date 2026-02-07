package com.clinic.medical_clinic_scheduler.mapper;

import com.clinic.medical_clinic_scheduler.dto.PatientCreateDTO;
import com.clinic.medical_clinic_scheduler.model.Patient;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class PatientMapperTest {

    // retrieve mapper instance manually
    private final PatientMapper mapper = Mappers.getMapper(PatientMapper.class);

    @Test
    void shouldMapDtoToEntity() {
        // given
        PatientCreateDTO dto = PatientCreateDTO.builder()
                .firstName("John")
                .lastName("Wick")
                .email("john@wick.com")
                .password("dog")
                .phoneNumber("123456789")
                .build();

        // when
        Patient entity = mapper.toEntity(dto);

        // then
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getEmail()).isEqualTo("john@wick.com");
        assertThat(entity.getPassword()).isEqualTo("dog"); // password is mapped directly (hashed in service)
        assertThat(entity.getId()).isNull(); // id should be null
    }
}