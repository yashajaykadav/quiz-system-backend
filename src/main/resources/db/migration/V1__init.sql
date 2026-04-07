-- 1. Create Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    role ENUM('ADMIN', 'STUDENT') NOT NULL,
    last_login DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create Subjects Table
CREATE TABLE subjects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- 3. Create Topics Table
CREATE TABLE topics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT,
    name VARCHAR(100) NOT NULL,
    FOREIGN KEY (subject_id) REFERENCES subjects(id)
);

-- 4. Create Questions Table
CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    topic_id BIGINT,
    content TEXT NOT NULL,
    type ENUM('OBJECTIVE', 'DESCRIPTIVE') DEFAULT 'OBJECTIVE',
    option_a VARCHAR(255),
    option_b VARCHAR(255),
    option_c VARCHAR(255),
    option_d VARCHAR(255),
    correct_answer VARCHAR(255),
    marks INT DEFAULT 1,
    FOREIGN KEY (topic_id) REFERENCES topics(id)
);

-- 5. Create Quizzes Table
CREATE TABLE quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT,
    topic_id BIGINT,
    title VARCHAR(150) NOT NULL,
    duration_minutes INT NOT NULL,
    scheduled_date DATE NOT NULL,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (topic_id) REFERENCES topics(id)
);

-- 6. Create Quiz Results Table
CREATE TABLE results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    quiz_id BIGINT,
    marks_obtained INT,
    total_marks INT,
    percentage DOUBLE,
    status ENUM('COMPLETED', 'MISSED') DEFAULT 'COMPLETED',
    attempt_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);

-- 7. Insert a default Admin (Password is 'admin123' encoded with BCrypt)
-- You can change this later!
INSERT INTO users (username, password, full_name, role) 
VALUES ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7uqqQ8a', 'System Admin', 'ADMIN');