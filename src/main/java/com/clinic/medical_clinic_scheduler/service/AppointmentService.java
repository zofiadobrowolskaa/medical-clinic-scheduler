package com.clinic.medical_clinic_scheduler.service;

import com.clinic.medical_clinic_scheduler.dto.AppointmentDTO;
import com.clinic.medical_clinic_scheduler.dto.ScheduleRequestDTO;
import java.util.List;

public interface AppointmentService {
    List<AppointmentDTO> createSchedule(ScheduleRequestDTO scheduleRequestDTO);
}