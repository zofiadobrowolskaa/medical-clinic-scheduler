package com.clinic.medical_clinic_scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Override
    @Async
    public void sendAppointmentConfirmation(String to, String subject, String content) {
        log.info("Starting email sending process to: {}", to);

        try {
            // todo: connect real email service (JavaMailSender)
            Thread.sleep(2000);

            log.info("Email sent successfully to: {} with subject: {}", to, subject);
            log.debug("Email content: {}", content);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Email sending interrupted", e);
        } catch (Exception e) {
            // exceptions in @Async void methods are not propagated to the caller,
            // so manual logging is required to track failures.
            log.error("Failed to send email to {}", to, e);
        }
    }
}