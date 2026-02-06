package com.clinic.medical_clinic_scheduler.config;

import com.clinic.medical_clinic_scheduler.repository.DoctorRepository;
import com.clinic.medical_clinic_scheduler.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/patients"
                        ).permitAll()
                        .requestMatchers("/api/doctors/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/appointments/schedule").hasAuthority("ROLE_DOCTOR")
                        .requestMatchers("/api/appointments/book/**").hasAuthority("ROLE_PATIENT")
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

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
                        .roles(patient.get().getRole())
                        .build();
            }

            var doctor = doctorRepository.findAll().stream()
                    .filter(d -> d.getEmail().equals(username))
                    .findFirst();
            if (doctor.isPresent()) {
                return User.builder()
                        .username(doctor.get().getEmail())
                        .password(doctor.get().getPassword())
                        .roles(doctor.get().getRole())
                        .build();
            }

            if ("admin@clinic.com".equals(username)) {
                return User.builder()
                        .username("admin@clinic.com")
                        .password(passwordEncoder().encode("admin123"))
                        .roles("ADMIN")
                        .build();
            }

            throw new UsernameNotFoundException("User not found");
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());

        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}