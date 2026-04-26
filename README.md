# 🎪 Tomorrowland Shop

> Java web application for purchasing Tomorrowland tickets online, built with Spring Boot, Spring MVC, Thymeleaf, Spring Security, and PostgreSQL.

---

## 🌐 Live Demo

**Production:** [tomorrowland-project-production.up.railway.app](https://tomorrowland-project-production.up.railway.app)

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Buyer | register at `/register` | your choice |

---

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Running Tests](#running-tests)
- [Deployment](#deployment)
- [Project Structure](#project-structure)

---

## ✨ Features

### Anonymous Users
- Browse products and categories
- Add items to session-based cart
- Update quantities or remove items

### Authenticated Buyers (ROLE_USER)
- Register and login
- Checkout via PayPal or Cash on Delivery
- View order history

### Administrators (ROLE_ADMIN)
- Manage products and categories
- View all orders with filters
- View login audit log

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 3 |
| Web | Spring MVC + Thymeleaf |
| Security | Spring Security + JWT |
| Database | PostgreSQL |
| ORM | Spring Data JPA + Hibernate |
| Migrations | Flyway |
| Payment | PayPal Java SDK |
| Build | Maven + Docker |
| Hosting | Railway |
| Testing | JUnit 5 + Mockito + MockMvc |
| UI | Bootstrap 5 |

---

## 🏗 Architecture

```text
Browser
   ↓
Spring Security (session + JWT filter)
   ↓
Controllers (MVC + API)
   ↓
Service layer (@Transactional business logic)
   ↓
Repositories (JPA/Hibernate)
   ↓
PostgreSQL
```

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+

### Run locally

```bash
./mvnw spring-boot:run
```

App runs at `http://localhost:8080`.

### Run with Docker

```bash
docker build -t tomorrowland-shop .
docker run -p 8080:8080 tomorrowland-shop
```

---

## 🧪 Running Tests

```bash
# all tests
./mvnw test

# full verification
./mvnw clean verify
```

---

## ☁️ Deployment

Deployed on Railway with PostgreSQL and environment-based configuration.

Typical env vars:
- `SPRING_PROFILES_ACTIVE`
- `DATABASE_URL`
- `JWT_SECRET`
- `PAYPAL_CLIENT_ID`
- `PAYPAL_CLIENT_SECRET`
- `PAYPAL_MODE`

---

## 📁 Project Structure

```text
src/
├── main/
│   ├── java/com/yourname/tomorrowlandshop/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── controller/api/
│   │   ├── domain/
│   │   ├── repository/
│   │   └── service/
│   └── resources/
│       ├── db/migration/
│       ├── templates/
│       └── application*.properties
└── test/
    └── java/com/yourname/tomorrowlandshop/
```

---

## 👨‍💻 Project

University Java Web project.
