```markdown
# 📝 Quiz System Backend

A high-performance, secure, and scalable RESTful API built with **Spring Boot 3.2**. This system is designed to power interactive educational platforms with real-time quiz evaluation, role-based security, and advanced anti-cheat mechanisms.

---

## 🏗️ Architecture & Best Practices

The project follows a modular **Layered Architecture** to ensure a clean separation of concerns:

* **Controller Layer**: Handles RESTful HTTP requests, DTO mapping, and input validation.
* **Service Layer**: Encapsulates core business logic, such as quiz result calculation and anti-cheat tracking.
* **Repository Layer**: Uses Spring Data JPA for optimized MySQL interaction and database abstraction.
* **Security Layer**: Implements stateless **JWT (JSON Web Token)** authentication and authorization.

---

## 🛠️ Technology Stack

| Category | Technology |
| :--- | :--- |
| **Language** | **Java 17** |
| **Framework** | **Spring Boot 3.2.0** |
| **Security** | **Spring Security & JJWT** |
| **Database** | **MySQL** with HikariCP Connection Pooling |
| **Migrations** | **Flyway DB** for version-controlled schema updates |
| **Caching** | **Spring Cache** (ConcurrentMap) for results and metadata |
| **Documentation** | **SpringDoc OpenAPI (Swagger UI)** |
| **Utilities** | **Lombok**, **Java-Dotenv** (Environment Management) |

---

## 🌟 Key Features

### 🔒 Secure Role-Based Access
Strict isolation between `ADMIN` and `STUDENT` roles. Admins manage the content (Subjects, Topics, Questions, Quizzes), while students access personalized dashboards.

### 🕒 Intelligent Quiz Engine
* **Session Management**: Prevents multiple attempts for the same quiz per student.
* **Auto-Grading**: Instant calculation of obtained marks and percentage upon submission.
* **Today's Quizzes**: Dynamically fetches active quizzes based on the current date.

### 🛡️ Anti-Cheat System
Includes a built-in **Warning Tracker**. If a student switches tabs or loses focus during a quiz, the system records a warning. After **3 warnings**, the quiz is automatically submitted to prevent malpractice.

### 📊 Performance Analytics
Comprehensive student dashboard metrics, including average scores, best performance, and total quizzes attempted.

---

## 🚀 Getting Started

### 1. Prerequisites
* **JDK 17** or higher.
* **Maven 3.6+**.
* **MySQL Server**.

### 2. Environment Configuration
The project uses `java-dotenv` to manage secrets. Create a `.env` file in the root directory:
```env
PORT=8081
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/quiz_db
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_db_password
JWT_SECRET=your_base64_high_entropy_secret_key
```

### 3. Build and Run
```bash
# Clone the repository
git clone [https://github.com/yashajaykadav/quiz-system-backend.git](https://github.com/yashajaykadav/quiz-system-backend.git)

# Navigate into the directory
cd quiz-system-backend

# Install dependencies and build the JAR
mvn clean install

# Launch the application
mvn spring-boot:run
```
The application will be available at `http://localhost:8081`.

---

## 📡 API Documentation
Once the application is running, you can explore and test the APIs via Swagger UI:
👉 `http://localhost:8081/swagger-ui/index.html`

---

## 🧪 Testing & Quality
Run the JUnit 5 test suite with the following command:
```bash
mvn test
```
The project uses **JaCoCo** to generate code coverage reports for the service layer.

---

## 📜 License
This project is licensed under the **MIT License**.
```
