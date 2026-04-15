# Quiz System Backend

Welcome to the Quiz System Backend project! This project provides a robust backend architecture for managing quizzes, user interactions, and analytics. It is designed with best practices in mind, particularly focusing on Spring Boot fundamentals.

## Project Architecture

The project follows a layered architecture, including:
- **Controller Layer**: Handles HTTP requests and responses, translating incoming data to domain objects and vice versa.
- **Service Layer**: Contains business logic and communicates with repositories for data persistence.
- **Repository Layer**: Utilizes Spring Data JPA to manage data access and CRUD operations.

This architecture promotes separation of concerns, making the system modular and maintainable.

## Technologies Used  
- **Java**: The primary programming language for developing the backend.
- **Spring Boot**: A framework that simplifies the setup and development of new applications.  
- **Spring Data JPA**: For data access and manipulation.
- **H2 Database**: An in-memory database for development and testing.  
- **JUnit & Mockito**: For unit and integration testing.
- **Lombok**: To reduce boilerplate code in model classes.
- **Maven**: For project management and dependency handling.

## Key Features
- **User Management**: Authenticate and manage user accounts and profiles.
- **Quiz Management**: Create, update, and delete quizzes.
- **Question Management**: Support various question types (MCQ, True/False, etc.).
- **Results Tracking**: Users can view their results and progress.
- **RESTful API**: Provides endpoints that facilitate integration with frontend applications.

## Spring Boot Best Practices
- **Configuration Management**: Externalized configuration using `application.properties` for different environments (development, testing, production).
- **Exception Handling**: Centralized error handling using `@ControllerAdvice`.  
- **Validation**: Input validation using Hibernate Validator (JSR-303).
- **Unit and Integration Testing**: Comprehensive coverage using JUnit and Mockito.
- **API Documentation**: Utilization of Swagger UI for generating interactive API documentation.

## Getting Started
1. Clone the repository.
   ```bash
   git clone https://github.com/yashajaykadav/quiz-system-backend.git
   ```
2. Navigate into the project directory.
   ```bash
   cd quiz-system-backend
   ```
3. Build the project using Maven.
   ```bash
   mvn clean install
   ```
4. Run the application.
   ```bash
   mvn spring-boot:run
   ```

## Contribution
We welcome contributions! Please feel free to fork the repository and submit pull requests.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.