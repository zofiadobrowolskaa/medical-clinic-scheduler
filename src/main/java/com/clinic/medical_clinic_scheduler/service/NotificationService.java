package com.clinic.medical_clinic_scheduler.service;

public interface NotificationService {
    void sendAppointmentConfirmation(String toEmail, String subject, String content);
}