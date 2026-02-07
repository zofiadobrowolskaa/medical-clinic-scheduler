package com.clinic.medical_clinic_scheduler.controller;

import com.clinic.medical_clinic_scheduler.config.JwtAuthenticationFilter;
import com.clinic.medical_clinic_scheduler.config.JwtService;
import com.clinic.medical_clinic_scheduler.config.SecurityConfig;
import com.clinic.medical_clinic_scheduler.dto.AppointmentDTO;
import com.clinic.medical_clinic_scheduler.dto.BookAppointmentDTO;
import com.clinic.medical_clinic_scheduler.model.AppointmentStatus;
import com.clinic.medical_clinic_scheduler.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    // mock security dependencies required by JwtAuthenticationFilter
    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "patient@test.com", roles = "PATIENT")
    void shouldReturn200WhenBookingAppointment() throws Exception {
        // given
        Long appointmentId = 1L;
        BookAppointmentDTO request = new BookAppointmentDTO(100L);

        AppointmentDTO responseDTO = AppointmentDTO.builder()
                .id(appointmentId)
                .status(AppointmentStatus.BOOKED)
                .startTime(LocalDateTime.now().plusDays(1))
                .build();

        // use refEq to match object fields since instances differ
        when(appointmentService.bookAppointment(eq(appointmentId), refEq(request)))
                .thenReturn(responseDTO);

        // when & then
        mockMvc.perform(patch("/api/appointments/{id}/book", appointmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())) // csrf token required in tests
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BOOKED"));
    }

    @Test
    @WithMockUser(username = "patient@test.com", roles = "PATIENT")
    void shouldReturn400WhenBookingRequestIsInvalid() throws Exception {
        // given
        // create invalid request (null patientId) to trigger @NotNull validation
        BookAppointmentDTO invalidRequest = new BookAppointmentDTO(null);

        // when & then
        mockMvc.perform(patch("/api/appointments/1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest()); // expect validation error
    }

    @Test
    @WithMockUser(username = "hacker@test.com", roles = "PATIENT")
    void shouldReturn403WhenPatientTriesToCreateSchedule() throws Exception {
        // given
        // endpoint /schedule is restricted to ROLE_DOCTOR only
        com.clinic.medical_clinic_scheduler.dto.ScheduleRequestDTO request =
                new com.clinic.medical_clinic_scheduler.dto.ScheduleRequestDTO(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), 30);

        // when & then
        mockMvc.perform(post("/api/appointments/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isForbidden()); // expect access denied
    }
}