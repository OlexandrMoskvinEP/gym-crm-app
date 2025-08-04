Gym CRM Application

## Prerequisites

To run this application, you should have the following installed:

- **Java Development Kit (JDK)** -- Oracle Open JDK 17.0.12
- **Maven** -- Apache Maven 3.9.9
- **Git** -- 2.49.0.windows.1

## Setup Instructions

Run the following script to create the database and add a user:

```sql
CREATE
DATABASE "gym_crm_db";
CREATE
USER gym WITH PASSWORD 'gym';
GRANT ALL PRIVILEGES ON DATABASE
"gym" TO gym;
```

### ⚙️ Running the application

After building the project you can start the server using Spring Boot:

```bash
  mvn spring-boot:run
```

Alternatively, run the packaged jar located in the `target` directory:

```bash
   java -jar target/gym-crm-app-1.0-SNAPSHOT.jar
```

## 🌐 Spring application Profiles

The application supports multiple Spring profiles to manage environment-specific configurations. All profiles use
PostgreSQL as the database backend.

| Profile | Purpose             | 
|---------|---------------------|
| `local` | Local testing       |
| `dev`   | Development profile | 
| `stg`   | Staging environment | 
| `prod`  | Production          | 

### How to activate a Spring profile

You can activate a profile in different ways depending on how you run the application:

#### ▶️ Via command line (JVM parameter)

```bash
  -Dspring.profiles.active=dev
```

### Using Maven Spring Boot plugin:

```bash
  -mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### From IntelliJ IDEA:

You can activate a profile by editing the Run/Debug Configuration:

Go to Run → Edit Configurations

Select your run target (e.g., GymCrmApplication)

In the "VM options" field, add:

```bash
  -Dspring.profiles.active=dev
```

## 📚 API Documentation

Interactive and downloadable documentation for the Gym CRM REST API.

### Swagger UI

Use Swagger UI to explore and test the available API endpoints.

- [Swagger UI](http://localhost:8080/swagger-ui/index.html)
- [OpenAPI JSON](http://localhost:8080/v3/api-docs)
- [OpenAPI YAML](http://localhost:8080/v3/api-docs.yaml)

> These URLs assume the application is running locally on port 8080.

---

### OpenAPI Specification (YAML)

This YAML file defines the full REST API contract and is used for client/server code generation.

- [OpenAPI YAML File](src/main/resources/openapi/gym.yaml)

> Used with `openapi-generator-maven-plugin` to generate DTOs and API interfaces.

---

### Postman Collection

You can also test the API using Postman.

- [Download Postman Collection](src/main/resources/postman/gym-crm-api.collection.json)

> To import the collection into Postman, open Postman, click `Import`, then select the `.json` file above.
>
---
> ## 📊 Health Monitoring (Spring Boot Actuator)

The application exposes a health endpoint powered by Spring Boot Actuator:

- [🔗Health check you can see here](http://localhost:8080/actuator/health)

### 📋 What it shows:

The `/actuator/health` endpoint provides aggregated status of critical system components:

| Component            | Description                                              |
|----------------------|----------------------------------------------------------|
| `db` / `dataBase`    | Database connection check (e.g. PostgreSQL or H2)        |
| `diskSpace`          | Built-in disk space indicator from Spring Boot           |
| `memory`             | 🛠 Custom indicator for free JVM memory (with threshold) |
| `diskSpaceIndicator` | 🛠 Custom indicator for free disk space (with threshold) |
| `ping`               | Basic always-up check (for liveness probes)              |

### Prometheus

Prometheus collects application metrics via the Spring Boot Actuator endpoint `/actuator/prometheus`.

- **Prometheus UI**: [http://localhost:9090](http://localhost:9090)
- **Application Metrics Endpoint
  **: [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)

### Grafana

Grafana is used to visualize metrics collected by Prometheus.

- **Grafana UI**: [http://localhost:3000](http://localhost:3000)  
  Default credentials: `admin` / `admin`
- Dashboard **GYM_CRM** displays runtime statistics such as login success/failure counts, DB errors, request rates, and
  more.

---
> ## Monitoring & Containerized Environment

This project includes full containerized monitoring and infrastructure:

- Spring Boot application
- PostgreSQL database
- Prometheus metrics collection
- Grafana dashboards

All services are launched together using:

```bash
   docker compose up -d
```
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=OlexandrMoskvinEP_gym-crm-app&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=OlexandrMoskvinEP_gym-crm-app)[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=OlexandrMoskvinEP_gym-crm-app&metric=coverage)](https://sonarcloud.io/summary/new_code?id=OlexandrMoskvinEP_gym-crm-app)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=OlexandrMoskvinEP_gym-crm-app&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=OlexandrMoskvinEP_gym-crm-app)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=OlexandrMoskvinEP_gym-crm-app&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=OlexandrMoskvinEP_gym-crm-app)
