CREATE TABLE doctor (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    specialization VARCHAR(50) NOT NULL
);

CREATE TABLE appointment (
    id BIGSERIAL PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT,

    CONSTRAINT fk_appointment_doctor
     FOREIGN KEY (doctor_id)
         REFERENCES doctor(id),

    CONSTRAINT fk_appointment_patient
     FOREIGN KEY (patient_id)
         REFERENCES patient(id)
);