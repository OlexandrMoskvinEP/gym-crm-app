# Gym CRM – REST API Endpoints

Full list of  REST endpoints

---

##  Authentication

| Method | Path               | Description                     |
|--------|--------------------|---------------------------------|
| POST   | /trainees/register | Register a new trainee          |
| POST   | /trainers/register | Register a new trainer          |
| GET    | /login             | Authenticate by username/pass   |
| PUT    | /login/change      | Change user password            |

---

## Trainee API

| Method | Path                                                   | Description                                         |
|--------|--------------------------------------------------------|-----------------------------------------------------|
| GET    | /trainees/{username}                                   | Get trainee profile                                 |
| PUT    | /trainees/{username}                                   | Update trainee profile                              |
| DELETE | /trainees/{username}                                   | Delete trainee profile (cascade trainings)          |
| PATCH  | /trainees/{username}/activation                        | Activate/deactivate trainee                         |
| PUT    | /trainees/{username}/trainers                          | Update trainee's trainer list                       |
| GET    | /trainees/{username}/trainers/not-assigned             | Get active trainers not assigned to this trainee    |
| GET    | /trainees/{username}/trainings                         | Get trainee trainings with filters                  |

---

##  Trainer API

| Method | Path                                                   | Description                                         |
|--------|--------------------------------------------------------|-----------------------------------------------------|
| GET    | /trainers/{username}                                   | Get trainer profile                                 |
| PUT    | /trainers/{username}                                   | Update trainer profile                              |
| PATCH  | /trainers/{username}/activation                        | Activate/deactivate trainer                         |
| GET    | /trainers/{username}/trainings                         | Get trainer trainings with filters                  |

---

##  Training API

| Method | Path              | Description                        |
|--------|-------------------|------------------------------------|
| POST   | /trainings        | Add new training                   |
| GET    | /training-types   | Get list of training types (const) |

---

## Notes

- `username` is unique and immutable.
- Only registration endpoints work without auth.
- Deleting a trainee cascades deletion of their trainings.
- Activation is not idempotent.
- All endpoints require logging with a `transactionId`.
- Input should be validated; unified error handling required.