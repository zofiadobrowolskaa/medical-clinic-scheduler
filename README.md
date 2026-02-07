# Medical Clinic Scheduler

[![Java](https://img.shields.io/badge/Java-17-red.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://www.docker.com/)

> **Professional medical clinic appointment booking system with advanced doctor schedule management, JWT authentication, and concurrency control.**

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Requirements](#requirements)
- [Installation and Setup](#installation-and-setup)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Testing](#testing)
- [Database Model](#database-model)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)

---

## Project Overview

**Medical Clinic Scheduler** is a comprehensive appointment management system for medical clinics, designed with three user roles in mind:

- **Doctors** - create availability schedules
- **Patients** - book and manage appointments
- **Administrators** - manage doctors and patients

The application leverages modern backend technologies with a strong emphasis on **security**, **performance**, and **scalability**.

---

## Features

### Authentication and Authorization
- **JWT (JSON Web Token)** for secure authorization
- **Role-based access control** (RBAC) with three roles: `PATIENT`, `DOCTOR`, `ADMIN`
- Password hashing using **BCrypt**
- Patient registration endpoint without authentication
- Login endpoint returning JWT token

### Schedule Management
- Doctors can create availability schedules with configurable slot duration
- Automatic generation of available appointment slots
- Search for available slots by doctor and date

### Appointment Booking
- Appointment booking by patients
- Appointment cancellation with time validation
- **Optimistic Locking** - prevents double booking of the same slot
- Patient appointment history

### Notifications
- **Asynchronous** email notifications after booking
- Spring `@Async` for non-blocking communication

### Soft Delete
- Logical deletion of patients and doctors (preserving historical data)
- Automatic exclusion of deleted records from queries

### Validation and Error Handling
- **Jakarta Bean Validation** for API requests
- **Global exception handler** with clear error messages
- Dedicated business exceptions (`ResourceNotFoundException`, `ActionNotAllowedException`, etc.)
- Standard error response format with timestamp and details

### Pagination
- Pagination support for doctor and patient lists
- Configurable sorting and page sizes

### Database Migrations
- **Flyway** for database schema versioning
- 5 migrations covering complete schema evolution

---

## Architecture

The project uses a **layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│          Controllers (REST API)         │  ← @RestController
├─────────────────────────────────────────┤
│              DTOs                       │  ← Data Transfer Objects
├─────────────────────────────────────────┤
│         Services (Business Logic)       │  ← @Service
├─────────────────────────────────────────┤
│           Mappers (MapStruct)           │  ← Entity ↔ DTO
├─────────────────────────────────────────┤
│     Repositories (Data Access)          │  ← Spring Data JPA
├─────────────────────────────────────────┤
│         Entities (Domain Model)         │  ← @Entity
├─────────────────────────────────────────┤
│       PostgreSQL Database               │
└─────────────────────────────────────────┘
```

### Design Patterns Used:
- **Dependency Injection** (Spring IoC)
- **Repository Pattern** (Spring Data JPA)
- **DTO Pattern** (layer separation)
- **Builder Pattern** (Lombok `@Builder`)
- **Strategy Pattern** (Spring Security Authentication)

---

## Technologies

### Backend Framework
- **Spring Boot 3.2.4**
- Spring Web (REST API)
- Spring Data JPA (ORM)
- Spring Security (authorization + JWT)
- Spring Validation (request validation)

### Database
- **PostgreSQL 15** (production)
- **H2** (in-memory tests)
- **Flyway** (migrations)

### Security
- **JWT** (io.jsonwebtoken:jjwt 0.11.5)
- **BCrypt** (Spring Security)

### Tools
- **Lombok** - boilerplate reduction
- **MapStruct 1.5.5** - entity ↔ DTO mapping
- **SpringDoc OpenAPI 2.3.0** - Swagger documentation
- **Maven 3.9.12** - build tool

### Testing
- **JUnit 5** - testing framework
- **Mockito** - dependency mocking
- **Spring Boot Test** - integration tests
- **AssertJ** - fluent assertions

### Deployment
- **Docker** - application containerization
- **Docker Compose** - orchestration (app + PostgreSQL)

---

## Requirements

Before running the project, make sure you have installed:

- **Java 17** or newer
- **Maven 3.9+**
- **Docker** and **Docker Compose**
- **PostgreSQL 15** (optional, if not using Docker)

---

## Installation and Setup

### Running with Docker Compose (recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/zofiadobrowolskaa/medical-clinic-scheduler.git
   cd medical-clinic-scheduler
   ```

2. **Configure Environment**

Create a ```.env``` file in the project root directory containing your JWT secret:

```
JWT_SECRET=your_secure_base64_encoded_secret_key_here
```

**Note:** The key must be at least 256-bit / 32 bytes to work with HS256 algorithm

3. **Run the application**

Execute the following command to build the image and start services:
   ```bash
   docker-compose up --build
   ```

4. **Access the Application**

The application will start on port ```8080```.
   - API: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui/index.html`
   - Database: Accessible locally at ```localhost:5433``` (User/Pass: ```postgres```/```postgres```)

---

## Configuration

### application.yaml

The main configuration file is located at `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/medical_clinic
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate  # Flyway manages schema
    show-sql: true
  flyway:
    enabled: true
    baseline-on-migrate: true

application:
  security:
    jwt:
      secret-key: ${JWT_SECRET}
      expiration: 86400000  # 24 hours
```

### Environment Variables

| Variable | Description | Default Value |
|---------|------|------------------|
| `JWT_SECRET` | Key for signing JWT tokens (required) | - |
| `SPRING_DATASOURCE_URL` | Database URL | `jdbc:postgresql://localhost:5433/medical_clinic` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `postgres` |

---

## API Endpoints

### Public (no authorization required)

| Method | Endpoint | Description |
|--------|----------|------|
| `POST` | `/api/auth/login` | User login (returns JWT token) |
| `POST` | `/api/patients` | New patient registration |

### ROLE_PATIENT (JWT token required)

| Method | Endpoint | Description |
|--------|----------|------|
| `PATCH` | `/api/appointments/{id}/book` | Book appointment |
| `GET` | `/api/appointments/patient/{patientId}` | List patient appointments |
| `PATCH` | `/api/appointments/{id}/cancel` | Cancel appointment |
| `GET` | `/api/appointments/search?doctorId=X&date=YYYY-MM-DD` | Search available slots |

### ROLE_DOCTOR (JWT token required)

| Method | Endpoint | Description |
|--------|----------|------|
| `POST` | `/api/appointments/schedule` | Create appointment schedule |

### ROLE_ADMIN (JWT token required)

| Method | Endpoint | Description |
|--------|----------|------|
| `POST` | `/api/doctors` | Add new doctor |
| `GET` | `/api/doctors` | List doctors (paginated) |
| `DELETE` | `/api/doctors/{id}` | Delete doctor (soft delete) |
| `GET` | `/api/patients` | List patients (paginated) |
| `DELETE` | `/api/patients/{id}` | Delete patient (soft delete) |

---

## Security

### JWT Authentication

1. **Login:**
   ```bash
   POST /api/auth/login
   {
     "email": "patient@test.com",
     "password": "password123"
   }
   ```

   Response:
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   }
   ```

2. **Using token in subsequent requests:**
   ```bash
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

### Default Administrator Account

- **Email:** `admin@clinic.com`
- **Password:** `admin123`
- **Role:** `ADMIN`

WARNING: Change the administrator password in production environment!

### Authorization Rules

```java
/api/auth/**           → permitAll()
/api/patients (POST)   → permitAll() (registration)
/api/doctors/**        → ROLE_ADMIN
/api/appointments/schedule → ROLE_DOCTOR
/api/appointments/*/book   → ROLE_PATIENT
```

---

## Testing

The project contains a **comprehensive test suite** at various levels:

### Running Tests

```bash
./mvnw test
```

### Test Types

#### 1. **Unit Tests** (`AppointmentServiceImplTest`)
- Testing business logic in isolation
- Dependency mocking (Mockito)
- Verification of service correctness

#### 2. **Integration Tests** (`AppointmentFlowIntegrationTest`)
- Testing complete application flow
- Using H2 in-memory database
- Verification of component cooperation

#### 3. **Controller Tests** (`AppointmentControllerTest`)
- Testing REST API with MockMvc
- Verification of security constraints
- Testing request validation

#### 4. **Mapper Tests** (`PatientMapperTest`)
- Verification of entity ↔ DTO mapping correctness
- MapStruct integration testing

### Example Optimistic Locking Test

```java
// Test verifies that concurrent booking of the same slot
// throws ObjectOptimisticLockingFailureException
@Test
void shouldFailWhenTwoPatientsTryToBookSameAppointment() {
    // Simulates race condition: two patients booking the same slot
    // Hibernate @Version prevents conflict
}
```

## Database Model

### ERD Diagram

```
┌──────────────┐         ┌──────────────────┐         ┌──────────────┐
│   PATIENT    │         │   APPOINTMENT    │         │    DOCTOR    │
├──────────────┤         ├──────────────────┤         ├──────────────┤
│ id (PK)      │         │ id (PK)          │         │ id (PK)      │
│ email        │◄────┐   │ start_time       │   ┌────►│ email        │
│ password     │     │   │ end_time         │   │     │ password     │
│ first_name   │     └───│ patient_id (FK)  │   │     │ first_name   │
│ last_name    │         │ doctor_id (FK)   │───┘     │ last_name    │
│ phone_number │         │ status           │         │ specialization│
│ role         │         │ version          │         │ role         │
│ deleted      │         └──────────────────┘         │ deleted      │
└──────────────┘                                      └──────────────┘
```

### Tables

#### `patient`
- **id** - PRIMARY KEY
- **email** - UNIQUE, NOT NULL
- **password** - varchar(255), BCrypt hashed
- **first_name**, **last_name**, **phone_number**
- **role** - ENUM: `PATIENT`
- **deleted** - BOOLEAN (soft delete)

#### `doctor`
- **id** - PRIMARY KEY
- **email** - UNIQUE, NOT NULL
- **password** - varchar(255), BCrypt hashed
- **first_name**, **last_name**
- **specialization** - e.g., "Cardiology", "Dermatology"
- **role** - ENUM: `DOCTOR`, `ADMIN`
- **deleted** - BOOLEAN (soft delete)

#### `appointment`
- **id** - PRIMARY KEY
- **start_time**, **end_time** - TIMESTAMP
- **status** - ENUM: `AVAILABLE`, `BOOKED`, `CANCELLED`, `COMPLETED`
- **doctor_id** - FOREIGN KEY → doctor(id)
- **patient_id** - FOREIGN KEY → patient(id), nullable
- **version** - BIGINT (optimistic locking)

### Flyway Migrations

| File | Description |
|------|------|
| `V1__init_patient_table.sql` | Create `patient` table |
| `V2__init_doctor_appointment_tables.sql` | Create `doctor` and `appointment` tables |
| `V3__add_version_to_appointment.sql` | Add `version` column (optimistic locking) |
| `V4__add_roles_and_doctor_password.sql` | Add roles and password for doctors |
| `V5__add_soft_delete.sql` | Add `deleted` column (soft delete) |

---

## API Documentation

### Swagger UI

After starting the application, API documentation is available at:

**http://localhost:8080/swagger-ui/index.html**

Swagger UI allows you to:
- Browse all endpoints
- Test API directly from the browser
- Authenticate using JWT (click "Authorize" and paste token)

### OpenAPI Specification

Raw OpenAPI 3.0 specification (JSON):

**http://localhost:8080/v3/api-docs**

---

## Project Structure

```
medical-clinic-scheduler/
├── src/
│   ├── main/
│   │   ├── java/com/clinic/medical_clinic_scheduler/
│   │   │   ├── config/              # Configuration (Security, JWT, OpenAPI)
│   │   │   │   ├── ApplicationConfig.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── JwtService.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/          # REST Controllers
│   │   │   │   ├── AppointmentController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── DoctorController.java
│   │   │   │   └── PatientController.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── AppointmentDTO.java
│   │   │   │   ├── BookAppointmentDTO.java
│   │   │   │   ├── DoctorCreateDTO.java
│   │   │   │   ├── DoctorDTO.java
│   │   │   │   ├── LoginRequestDTO.java
│   │   │   │   ├── LoginResponseDTO.java
│   │   │   │   ├── PatientCreateDTO.java
│   │   │   │   ├── PatientDTO.java
│   │   │   │   └── ScheduleRequestDTO.java
│   │   │   ├── exception/           # Custom Exceptions + Global Handler
│   │   │   │   ├── ActionNotAllowedException.java
│   │   │   │   ├── ApiError.java
│   │   │   │   ├── AppointmentConflictException.java
│   │   │   │   ├── DoctorAlreadyExistsException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── PatientAlreadyExistsException.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── mapper/              # MapStruct Mappers
│   │   │   │   ├── AppointmentMapper.java
│   │   │   │   ├── DoctorMapper.java
│   │   │   │   └── PatientMapper.java
│   │   │   ├── model/               # JPA Entities
│   │   │   │   ├── Appointment.java
│   │   │   │   ├── AppointmentStatus.java
│   │   │   │   ├── Doctor.java
│   │   │   │   ├── Patient.java
│   │   │   │   └── Role.java
│   │   │   ├── repository/          # Spring Data JPA Repositories
│   │   │   │   ├── AppointmentRepository.java
│   │   │   │   ├── DoctorRepository.java
│   │   │   │   └── PatientRepository.java
│   │   │   ├── service/             # Business Logic
│   │   │   │   ├── AppointmentService.java
│   │   │   │   ├── AppointmentServiceImpl.java
│   │   │   │   ├── DoctorService.java
│   │   │   │   ├── DoctorServiceImpl.java
│   │   │   │   ├── NotificationService.java
│   │   │   │   ├── NotificationServiceImpl.java
│   │   │   │   ├── PatientService.java
│   │   │   │   └── PatientServiceImpl.java
│   │   │   └── MedicalClinicSchedulerApplication.java
│   │   └── resources/
│   │       ├── application.yaml     # Main configuration
│   │       └── db/migration/        # Flyway migrations
│   │           ├── V1__init_patient_table.sql
│   │           ├── V2__init_doctor_appointment_tables.sql
│   │           ├── V3__add_version_to_appointment.sql
│   │           ├── V4__add_roles_and_doctor_password.sql
│   │           └── V5__add_soft_delete.sql
│   └── test/
│       ├── java/com/clinic/medical_clinic_scheduler/
│       │   ├── controller/
│       │   │   └── AppointmentControllerTest.java
│       │   ├── integration/
│       │   │   └── AppointmentFlowIntegrationTest.java
│       │   ├── mapper/
│       │   │   └── PatientMapperTest.java
│       │   ├── service/
│       │   │   └── AppointmentServiceImplTest.java
│       │   ├── DatabaseTest.java
│       │   └── MedicalClinicSchedulerApplicationTests.java
│       └── resources/
│           └── application-test.yaml  # Test configuration (H2)
├── target/                           # Build artifacts
├── .mvn/wrapper/                     # Maven Wrapper
├── docker-compose.yml                # Docker Compose configuration
├── Dockerfile                        # Multi-stage Docker build
├── pom.xml                           # Maven dependencies
├── mvnw                              # Maven Wrapper (Linux/Mac)
├── mvnw.cmd                          # Maven Wrapper (Windows)
└── README.md                         # This file
```

---

## Usage Examples

### Scenario 1: Patient Registration and Login

```bash
# 1. Register new patient
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "securePassword123",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "123456789"
  }'

# 2. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "securePassword123"
  }'

# Response: { "token": "eyJhbGc..." }
```

### Scenario 2: Doctor Creates Schedule

```bash
# Login as doctor
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@clinic.com",
    "password": "doctorPassword"
  }'

# Create schedule (2-hour window, 30-minute slots)
curl -X POST http://localhost:8080/api/appointments/schedule \
  -H "Authorization: Bearer <DOCTOR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "doctorId": 1,
    "startTime": "2026-02-15T10:00:00",
    "endTime": "2026-02-15T12:00:00",
    "slotDurationMinutes": 30
  }'

# Creates 4 slots: 10:00, 10:30, 11:00, 11:30
```

### Scenario 3: Patient Books Appointment

```bash
# 1. Search for available slots
curl -X GET "http://localhost:8080/api/appointments/search?doctorId=1&date=2026-02-15" \
  -H "Authorization: Bearer <PATIENT_TOKEN>"

# 2. Book selected slot (ID: 123)
curl -X PATCH http://localhost:8080/api/appointments/123/book \
  -H "Authorization: Bearer <PATIENT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 5
  }'

# 3. Check your appointments
curl -X GET http://localhost:8080/api/appointments/patient/5 \
  -H "Authorization: Bearer <PATIENT_TOKEN>"
```

---

## Roadmap (Future Features)

- [ ] Integration with real email system (SendGrid / SMTP)
- [ ] WebSocket notifications for real-time updates
- [ ] Reporting and statistics for administrators
- [ ] PDF export (appointment history)
- [ ] Doctor rating and review system
- [ ] SMS reminders before appointments
- [ ] Admin panel (frontend)
- [ ] Multi-language support (i18n)
- [ ] Elasticsearch for advanced search

---


