package com.clinic.medical_clinic_scheduler.config;

import com.clinic.medical_clinic_scheduler.model.Role;
import com.clinic.medical_clinic_scheduler.repository.DoctorRepository;
import com.clinic.medical_clinic_scheduler.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {

            var patient = patientRepository.findAll().stream()
                    .filter(p -> p.getEmail().equals(username))
                    .findFirst();
            if (patient.isPresent()) {
                return User.builder()
                        .username(patient.get().getEmail())
                        .password(patient.get().getPassword())
                        .roles(patient.get().getRole().name())
                        .build();
            }

            var doctor = doctorRepository.findAll().stream()
                    .filter(d -> d.getEmail().equals(username))
                    .findFirst();
            if (doctor.isPresent()) {
                return User.builder()
                        .username(doctor.get().getEmail())
                        .password(doctor.get().getPassword())
                        .roles(doctor.get().getRole().name())
                        .build();
            }

            if ("admin@clinic.com".equals(username)) {
                return User.builder()
                        .username("admin@clinic.com")
                        .password(passwordEncoder().encode("admin123"))
                        .roles(Role.ADMIN.name())
                        .build();
            }

            throw new UsernameNotFoundException("User not found");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
