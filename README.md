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

The application supports multiple Spring profiles to manage environment-specific configurations. All profiles use PostgreSQL as the database backend.

| Profile   | Purpose              | Description                                                                 |
|-----------|----------------------|-----------------------------------------------------------------------------|
| `local`   | Local testing         | Uses PostgreSQL instance for local development. Liquibase is disabled. Actuator fully enabled. Full `DEBUG` logging. |
| `dev`     | Development profile   | PostgreSQL-based environment with Liquibase enabled. Suitable for developers working with real data. Logging level is `DEBUG`. |
| `stg`     | Staging environment   | Pre-production PostgreSQL setup. Liquibase enabled. Logging level is reduced to `INFO`. Mimics production behavior for final testing. |
| `prod`    | Production            | Production-grade PostgreSQL environment. Liquibase is enabled. Logging is minimal. Actuator endpoints are restricted. Optimized for security and stability. |

###  How to activate a profile

You can activate a profile using:

** Command line:**
```  bash
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



