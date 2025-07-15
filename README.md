Gym CRM Application

## Prerequisites

To run this application, you should have the following installed:

- **Java Development Kit (JDK)** -- Oracle Open JDK 17.0.12
- **Maven** -- Apache Maven 3.9.9
- **Git** -- 2.49.0.windows.1

## Setup Instructions
Run the following script to create the database and add a user:

```sql
CREATE DATABASE "gym_crm_db";
CREATE USER gym WITH PASSWORD 'gym';
GRANT ALL PRIVILEGES ON DATABASE "gym" TO gym;
```
+
## API Reference

➡️ REST endpoints: [docs/ docs-gym-crm-rest-endpoints.md](docs/gym-crm-rest-endpoints.md)  
➡️ Swagger-spec: [openapi-spec.yaml](src/main/resources/openapi/gym-crm-rest.yaml)