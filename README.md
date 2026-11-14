# 🏦 Bank Card Management System

RESTful API service for managing bank cards, user accounts, and financial transfers securely.

---

## 📖 Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Environment Variables](#-environment-variables)
- [Getting Started](#-getting-started)
    - [Run with Docker](#run-with-docker)
    - [Local Development Setup](#local-development-setup)
- [API Documentation](#-api-documentation)
- [Key Endpoints](#-key-endpoints)
- [Error Handling & API Responses](#-error-handling--api-responses)
- [Testing](#-testing)

---

## ✨ Features

- **Authentication & Security:** JWT-based stateless authentication with role-based access control (`ROLE_USER`, `ROLE_ADMIN`).
- **Card Management:** Create, issue, view balance, and lock/unlock bank cards.
- **Transfers:** Execute secure financial transfers between cards with atomic transactional support.
- **Database Migrations:** Automated PostgreSQL schema management with Liquibase.
- **Standardized Error Handling:** RFC 7807 compliant structured JSON error responses.

---

## 🛠️ Tech Stack

- **Language:** Java 25
- **Framework:** Spring Boot 4
- **Security:** Spring Security & JWT (JSON Web Tokens)
- **Data Access:** Spring Data JPA / Hibernate
- **Database:** PostgreSQL
- **Migrations:** Liquibase
- **Containerization:** Docker & Docker Compose
- **Documentation:** OpenAPI 3 / Swagger UI
- **Build Tool:** Apache Maven

---

## ⚙️ Environment Variables

The application can be configured using environment variables (or via an `.env` file):

| Variable | Description | Default Value |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | JDBC database connection URL | `jdbc:postgresql://localhost:5432/bankdb` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `postgres` |
| `JWT_SECRET` | Secret key used for signing JWTs | *Base64-encoded secret* |
| `JWT_EXPIRATION` | Token expiration time in milliseconds | `86400000` (24 hours) |

---

## 🚀 Getting Started

### Clone Repository
```bash
git clone https://github.com/Tarantism7/bank_demo.git
cd bank_demo
```

### Configure database connection:
Ensure PostgreSQL is running and update your application-dev.yaml or set your local environment variables.

### Build the project:
```bash
  mvn clean install
```

### Run the application
```bash
    mvn spring-boot:run
```

### Run with Docker

The fastest way to run the application along with PostgreSQL is using Docker Compose:

1. **Build and start services:**
```bash
   docker-compose up --build -d
```
2.**Check application logs:**
```bash
  docker-compose logs -f app
```
3.**Stop services:**
```bash
  docker-compose down -v
```

## API Documentation
Once the application is running, you can interactively explore and test all REST API endpoints using Swagger UI:

Swagger UI: http://localhost:8080/swagger-ui.html

OpenAPI Specification: http://localhost:8080/v3/api-docs

Note: To invoke protected endpoints in Swagger UI, click the Authorize button in the top right and enter your Bearer token in the format: Bearer <your_jwt_token>.

## 🔗 Key Endpoints
### 🔑 Authentication
POST /api/v1/auth/register — Register a new account

POST /api/v1/auth/login — Authenticate user and receive JWT

### 💳 Cards (ROLE_USER & ROLE_ADMIN)
GET /api/v1/cards — Retrieve logged-in user's active cards

POST /api/v1/cards — Issue a new card (ROLE_ADMIN)

PATCH /api/v1/cards/{id}/status — Lock or unlock a card

### 💸 Transfers
POST /api/v1/transfers — Transfer money between accounts/cards

GET /api/v1/transfers/history — Fetch transaction history

## Testing
```bash
    mvn test
```

## ⚠️ Error Handling & API Responses

This application follows the **RFC 7807 (Problem Details for HTTP APIs)** standard for all error responses. Errors are formatted uniformly using Spring's `ProblemDetail`.

### Common Error Payload Structure

All error responses share the following core JSON format:

* **Validation Error (`400 BAD REQUEST`)**: Returned when request body/DTO field validations fail.
* **Business Rule Error (`400 BAD REQUEST` / `403 FORBIDDEN` / `404 NOT FOUND`)**: Returned when domain rules are violated (e.g., insufficient funds, blocked card, missing resource).
* **Internal Server Error (`500 INTERNAL SERVER ERROR`)**: Returned when unhandled system exceptions occur.

---

### Error Examples

#### 1. Validation Error (400 Bad Request)
Returned when a request body fails DTO validation rules (e.g., negative transfer amount or empty recipient account):

```json
{
  "type": "[https://bankdemo.com/errors/validation-error](https://bankdemo.com/errors/validation-error)",
  "title": "VALIDATION_ERROR",
  "status": 400,
  "detail": "One or more request fields failed validation.",
  "timestamp": "2026-07-22T21:00:00Z",
  "invalidFields": {
    "amount": "Transfer amount must be greater than 0",
    "recipientAccountId": "Recipient account cannot be empty"
  }
}
```
#### 2. Business Exception (400 Bad Request)
Returned when a business constraint is violated (e.g., attempting a transfer that exceeds available account balance):
```json
{
  "type": "[https://bankdemo.com/errors/insufficient_funds](https://bankdemo.com/errors/insufficient_funds)",
  "title": "INSUFFICIENT_FUNDS",
  "status": 400,
  "detail": "Account ACC123 has insufficient funds. Current balance: 50.00, Requested: 150.00",
  "timestamp": "2026-07-22T21:00:00Z"
}
```

#### 3. Internal System Error (500 Internal Server Error)
Returned when an unexpected error occurs on the server. Includes a unique errorReference ID for tracking in backend logs:
```json
{
  "title": "INTERNAL_SERVER_ERROR",
  "status": 500,
  "detail": "An unexpected error occurred. Please contact support with reference ID: c3a9f021-998a-4428-a42e-a50d2bb69ba1",
  "timestamp": "2026-07-22T21:00:00Z",
  "errorReference": "c3a9f021-998a-4428-a42e-a50d2bb69ba1"
}
```