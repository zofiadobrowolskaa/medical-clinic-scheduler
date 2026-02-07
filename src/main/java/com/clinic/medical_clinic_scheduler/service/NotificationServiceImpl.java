package com.clinic.medical_clinic_scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Override
    @Async
    public void sendAppointmentConfirmation(String toEmail, String subject, String content) {
        log.info("START: Sending email to {}", toEmail);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("EMAIL SENT -> To: {}, Subject: {}, Content: {}", toEmail, subject, content);
        log.info("END: Email process finished for {}", toEmail);
    }
}