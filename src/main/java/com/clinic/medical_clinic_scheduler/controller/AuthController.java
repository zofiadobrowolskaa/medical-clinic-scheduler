package com.clinic.medical_clinic_scheduler.controller;

import com.clinic.medical_clinic_scheduler.config.JwtService;
import com.clinic.medical_clinic_scheduler.dto.LoginRequestDTO;
import com.clinic.medical_clinic_scheduler.dto.LoginResponseDTO;
import jakarta.validation.Valid; // <-- Ważny import
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@Valid @RequestBody LoginRequestDTO request) { // <-- Dodano @Valid
        log.info("Authentication attempt for user: {}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userDetailsService.loadUserByUsername(request.getEmail());
        var jwtToken = jwtService.generateToken(user);

        log.info("User {} successfully authenticated", request.getEmail());

        return ResponseEntity.ok(LoginResponseDTO.builder().token(jwtToken).build());
    }
}