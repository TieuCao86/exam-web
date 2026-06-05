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
```

---

## 🚀 Installation

### 1. Clone or Extract Project
```powershell
cd "C:\Users\vietn\OneDrive\Máy tính\Project hè 2026\exam-web"
```

### 2. Set Up Database

Create MariaDB database and user (adjust credentials as needed):
```sql
CREATE DATABASE exam-management;
CREATE USER 'root'@'localhost' IDENTIFIED BY 'sapassword';
GRANT ALL PRIVILEGES ON exam-management.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

**Update `application.properties` if using different credentials:**
```properties
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
```

### 3. Install Frontend Dependencies
```powershell
cd .\frontend
pnpm install
```

### 4. Build Backend (Optional)
```powershell
.\mvnw clean package
# Or using installed Maven:
mvn clean package
```

---

## 💻 Development

### Quick Start (Both Frontend & Backend)

**Terminal 1 - Spring Boot Backend:**
```powershell
cd "C:\Users\vietn\OneDrive\Máy tính\Project hè 2026\exam-web"
.\mvnw spring-boot:run
# Or: mvn spring-boot:run
```
Backend runs at: **http://localhost:8080**

**Terminal 2 - React Frontend (Dev Server):**
```powershell
cd .\frontend
pnpm dev
# Or: npm run dev if using npm
```
Frontend dev server runs at: **http://localhost:5173** (or another port Vite suggests)

### Frontend Scripts
```powershell
cd .\frontend

# Start development server with hot reload
pnpm dev

# Build for production
pnpm build

# Preview production build locally
pnpm preview

# Run ESLint for code quality
pnpm lint
```

### Backend Development
- **Automatic Restart**: Dev Tools enabled (`spring.devtools.restart.enabled=true`)
- **Live Reload**: Enabled (`spring.devtools.livereload.enabled=true`)
- **SQL Logging**: Formatted SQL queries shown in console
- **Security Debugging**: Spring Security logs available (check console)

---

## 🏗️ Build & Deployment

### Production Build

#### 1. Build Frontend
```powershell
cd .\frontend
pnpm build
```
Output: `frontend/dist/` folder

#### 2. Copy Frontend Build to Backend
```powershell
# Clear old static files
Remove-Item -Recurse -Force ".\src\main\resources\static\*" -ErrorAction SilentlyContinue

# Copy new build
Copy-Item -Recurse -Force ".\frontend\dist\*" ".\src\main\resources\static\"
```

#### 3. Build Spring Boot JAR
```powershell
.\mvnw clean package -DskipTests
```
Output: `target/exam-web-0.0.1-SNAPSHOT.jar`

#### 4. Run Production JAR
```powershell
java -jar target/exam-web-0.0.1-SNAPSHOT.jar
```
Access at: **http://localhost:8080**

### Docker Deployment (Optional)
Create `Dockerfile` in project root:
```dockerfile
FROM openjdk:21-slim
WORKDIR /app
COPY target/exam-web-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```powershell
docker build -t exam-web:latest .
docker run -p 8080:8080 --env DATABASE_URL=jdbc:mariadb://mariadb:3306/exam-management exam-web:latest
```

---

## 📁 Project Structure

```
exam-web/
├── frontend/                          # React app (Vite)
│   ├── src/
│   │   ├── App.jsx                   # Main component
│   │   ├── main.jsx                  # Entry point
│   │   ├── index.css                 # Global styles
│   │   └── assets/                   # Images, fonts, etc.
│   ├── package.json                  # Dependencies
│   ├── vite.config.js                # Vite configuration
│   ├── eslint.config.js              # Linting rules
│   ├── index.html                    # HTML template
│   └── dist/                         # Built output (generated)
│
├── src/
│   ├── main/
│   │   ├── java/com/exam/exam_web/
│   │   │   ├── ExamWebApplication.java       # Main entry point
│   │   │   ├── api/
│   │   │   │   └── StudentExamApi.java      # REST API endpoints
│   │   │   ├── controller/
│   │   │   │   └── LoginController.java     # Auth controllers
│   │   │   ├── entity/                      # JPA entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Account.java
│   │   │   │   ├── Exam.java
│   │   │   │   ├── Question.java
│   │   │   │   ├── Answer.java
│   │   │   │   ├── Course.java
│   │   │   │   ├── Enrollment.java
│   │   │   │   └── ExamHistory.java
│   │   │   ├── dto/                         # Data Transfer Objects
│   │   │   │   ├── UserDTO.java
│   │   │   │   ├── ExamDTO.java
│   │   │   │   ├── QuestionDTO.java
│   │   │   │   └── ...
│   │   │   ├── mapper/                      # MapStruct mappers
│   │   │   │   ├── UserMapper.java
│   │   │   │   ├── ExamMapper.java
│   │   │   │   ├── QuestionMapper.java
│   │   │   │   └── ...
│   │   │   ├── repository/                  # Data access layer
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── ExamRepository.java
│   │   │   │   └── ...
│   │   │   ├── services/                    # Business logic
│   │   │   │   ├── UserService.java
│   │   │   │   ├── ExamService.java
│   │   │   │   └── ...
│   │   │   └── config/
│   │   │       ├── SecurityConfig.java      # Security configuration
│   │   │       ├── WebConfig.java           # Web MVC configuration
│   │   │       ├── DataSeeder.java          # Initial data
│   │   │       ├── RoleBasedSuccessHandler.java
│   │   │       └── interceptor/             # HTTP interceptors
│   │   ├── resources/
│   │   │   ├── application.properties       # App configuration
│   │   │   ├── static/                      # Served frontend (built)
│   │   │   │   ├── css/
│   │   │   │   ├── js/
│   │   │   │   └── index.html
│   │   │   └── templates/                   # Thymeleaf (legacy)
│   │   │       └── student/
│   │   │           ├── courses.html
│   │   │           ├── exams.html
│   │   │           └── ...
│   └── test/
│       └── java/...                         # Unit tests
│
├── pom.xml                                  # Maven dependencies
├── mvnw                                     # Maven wrapper (Linux/Mac)
├── mvnw.cmd                                 # Maven wrapper (Windows)
├── README.md                                # This file
└── .gitignore                               # Git ignored files

```

---

## 🔌 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Key Endpoints

#### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/login` | User login |
| `POST` | `/logout` | User logout |
| `GET` | `/user/profile` | Get current user profile |

#### Exams
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/exams` | Get all exams for student |
| `GET` | `/exams/{id}` | Get exam details |
| `POST` | `/exams/{id}/start` | Start an exam |
| `POST` | `/exams/{id}/submit` | Submit exam answers |

#### Courses
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/courses` | Get all available courses |
| `GET` | `/courses/{id}` | Get course details |
| `POST` | `/courses/{id}/enroll` | Enroll in course |

#### Questions & Answers
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/questions` | Get questions for exam |
| `POST` | `/answers` | Submit answer |

*For complete API spec, see `StudentExamApi.java`*

---

## 🗄️ Database

### Entity Relationships
```
User (1) ──────── (N) Account
  │
  ├─── (N) ────── (N) Course (via Enrollment)
  │
  ├─── (N) ────── ExamHistory
  │
  └─── (N) ────── ExamAnswer

Course (1) ────── (N) Exam
Exam (1) ────── (N) Question
Question (1) ────── (N) Answer
```

### Key Entities
- **User**: User account information
- **Account**: Login credentials (username, password, role)
- **Course**: Course information
- **Exam**: Exam details & configuration
- **Question**: Exam questions with multiple options
- **Answer**: Answer options for questions
- **ExamHistory**: Student exam attempt records
- **Enrollment**: Student course enrollment

### Database Setup
Database auto-creates on first run via JPA/Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

---

## ⚙️ Configuration

### Key Application Properties
```properties
# Database
spring.datasource.url=jdbc:mariadb://localhost:3306/exam-management
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update        # auto/create/create-drop/validate/update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Dev Tools
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true

# Server
server.port=8080
```

### Frontend Proxy (Development)
In `frontend/vite.config.js`, requests to `/api/*` are forwarded to Spring Boot:
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    },
  },
}
```

---

## 🐛 Troubleshooting

### Issue: Database Connection Failed
**Error**: `Access denied for user 'root'`
- **Cause**: MariaDB not running or wrong credentials
- **Fix**:
  1. Ensure MariaDB is running: `mysql -u root -p`
  2. Check credentials in `application.properties`
  3. Verify database exists: `SHOW DATABASES;`

### Issue: Port 8080 Already in Use
**Error**: `Address already in use: bind`
- **Fix**: Change port in `application.properties`:
  ```properties
  server.port=8081
  ```

### Issue: Frontend Shows Blank Page
**Cause**: Frontend dist not copied to `src/main/resources/static`
- **Fix**:
  ```powershell
  cd .\frontend
  pnpm build
  # Then copy dist to static (see Build & Deployment section)
  ```

### Issue: API Calls Return 403 Forbidden
**Cause**: Security config blocking API routes
- **Fix**: Check `SecurityConfig.java` — ensure `/api/**` routes are properly configured

### Issue: Hot Reload Not Working
- **Frontend**: Vite dev server should auto-reload (check `http://localhost:5173`)
- **Backend**: Restart may be needed for non-static file changes; ensure dev tools are enabled

### Issue: ESLint Errors in Frontend
```powershell
cd .\frontend
pnpm lint     # Check errors
```

---

## 📝 Development Workflow

### Making Changes
1. **Backend**: Edit Java files in `src/main/java/` → Spring Boot auto-restarts
2. **Frontend**: Edit React files in `frontend/src/` → Vite auto-refreshes
3. **Styles**: Edit CSS in `frontend/src/` or `src/main/resources/static/css/` → auto-reload

### Git Workflow
```powershell
git status
git add .
git commit -m "Feature: Add exam filtering"
git push origin main
```

**Branches**: 
- `main`: Production-ready code
- `develop`: Integration branch
- `feature/*`: Feature branches

---

## 📚 Additional Resources

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [Vite Guide](https://vitejs.dev/guide/)
- [MapStruct Manual](https://mapstruct.org/documentation/stable/reference/html/)
- [MariaDB Tutorial](https://mariadb.com/kb/en/getting-started/)

---

## 👨‍💻 Contributing

1. Create feature branch: `git checkout -b feature/YourFeature`
2. Commit changes: `git commit -m "Add feature"`
3. Push: `git push origin feature/YourFeature`
4. Open Pull Request

---

## 📄 License

This project is proprietary. All rights reserved.

---

## ❓ Support

For issues or questions:
- Check Troubleshooting section above
- Review Spring Boot logs: `target/logs/`
- Check browser DevTools (F12) for frontend errors
- Consult team documentation

---

**Last Updated**: June 2026

