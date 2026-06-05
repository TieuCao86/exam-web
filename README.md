# 8ThreadExamly - Exam Management Web Application

An online exam management system built with **Spring Boot** (backend) and **React** (frontend). Students can browse courses, take exams, view results, and track enrollment history.

## 📋 Table of Contents
- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Development](#development)
- [Build & Deployment](#build--deployment)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Database](#database)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Project Overview

**8ThreadExamly** is a student exam platform that provides:
- 📚 Course browsing and enrollment management
- 📝 Online exam taking with real-time feedback
- 📊 Exam history and result tracking
- 👤 Role-based access control (Student, Admin, etc.)
- 🔐 Secure authentication & authorization

### Key Features
- Student dashboard with calendar view
- Course details with exam listings
- Real-time exam answering system
- Enrollment status tracking
- Responsive UI with sidebar navigation

---

## 🛠 Tech Stack

### Backend
- **Framework**: Spring Boot 4.0.6
- **Java Version**: 21
- **Database**: MariaDB
- **ORM**: JPA (Hibernate)
- **Security**: Spring Security 6
- **Data Mapping**: MapStruct 1.5.5
- **Build Tool**: Maven

### Frontend
- **Library**: React 19.2.6
- **Build Tool**: Vite 8.0.12
- **Language**: JavaScript (ES6+)
- **Linting**: ESLint 10.3.0
- **Package Manager**: pnpm

### Additional Libraries
- Jackson (JSON processing)
- Lombok (Java boilerplate reduction)
- Thymeleaf (Server-side rendering, transitioning to React)

---

## 📦 Prerequisites

Ensure you have installed:
- **Java 21** ([Download](https://www.oracle.com/java/technologies/downloads/#java21))
- **Node.js 18+** & **pnpm** ([Download Node](https://nodejs.org/), then `npm install -g pnpm`)
- **MariaDB 10.5+** ([Download](https://mariadb.org/download/))
- **Maven 3.8+** (optional, use `./mvnw` included in project)

**Verify installations:**
```powershell
java -version
node --version
pnpm --version
# For Maven (if using ./mvnw):
.\mvnw --version
