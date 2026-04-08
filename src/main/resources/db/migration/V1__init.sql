-- ============================================
-- QUIZ BACKEND - AIVEN MYSQL SETUP
-- ============================================

-- Set character set and collation
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Disable foreign key checks for clean drop
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- DROP EXISTING TABLES
-- ============================================

DROP TABLE IF EXISTS student_answers;
DROP TABLE IF EXISTS quiz_attempts;
DROP TABLE IF EXISTS quiz_questions;
DROP TABLE IF EXISTS quizzes;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS topics;
DROP TABLE IF EXISTS subjects;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS flyway_schema_history;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- CREATE TABLES
-- ============================================

-- 1. Users Table
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       full_name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       active BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       last_login DATETIME(6),
                       INDEX idx_user_email (email),
                       INDEX idx_user_role (role),
                       INDEX idx_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Subjects Table
CREATE TABLE subjects (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) UNIQUE NOT NULL,
                          description TEXT,
                          created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                          created_by BIGINT,
                          INDEX idx_subject_name (name),
                          INDEX idx_subject_created_by (created_by),
                          FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Topics Table
CREATE TABLE topics (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        description TEXT,
                        subject_id BIGINT NOT NULL,
                        created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                        INDEX idx_topic_subject (subject_id),
                        INDEX idx_topic_name (name),
                        CONSTRAINT uk_topic_name_subject UNIQUE (name, subject_id),
                        FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Questions Table
CREATE TABLE questions (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           question_text TEXT NOT NULL,
                           code_snippet TEXT,
                           type VARCHAR(20) NOT NULL,
                           option1 VARCHAR(500) NOT NULL,
                           option2 VARCHAR(500) NOT NULL,
                           option3 VARCHAR(500) NOT NULL,
                           option4 VARCHAR(500) NOT NULL,
                           correct_option INT NOT NULL CHECK (correct_option BETWEEN 1 AND 4),
                           subject_id BIGINT NOT NULL,
                           topic_id BIGINT NOT NULL,
                           marks INT NOT NULL DEFAULT 1,
                           created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                           updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                           INDEX idx_question_subject (subject_id),
                           INDEX idx_question_topic (topic_id),
                           INDEX idx_question_type (type),
                           FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
                           FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Quizzes Table
CREATE TABLE quizzes (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         description TEXT,
                         subject_id BIGINT NOT NULL,
                         topic_id BIGINT NOT NULL,
                         duration_minutes INT NOT NULL CHECK (duration_minutes > 0),
                         scheduled_date DATETIME(6) NOT NULL,
                         total_marks INT NOT NULL,
                         status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
                         created_by BIGINT,
                         created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                         INDEX idx_quiz_subject (subject_id),
                         INDEX idx_quiz_topic (topic_id),
                         INDEX idx_quiz_status (status),
                         INDEX idx_quiz_scheduled_date (scheduled_date),
                         INDEX idx_quiz_created_by (created_by),
                         FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE RESTRICT,
                         FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE RESTRICT,
                         FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Quiz Questions (Many-to-Many)
CREATE TABLE quiz_questions (
                                quiz_id BIGINT NOT NULL,
                                question_id BIGINT NOT NULL,
                                PRIMARY KEY (quiz_id, question_id),
                                INDEX idx_qq_quiz (quiz_id),
                                INDEX idx_qq_question (question_id),
                                FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
                                FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Quiz Attempts Table
CREATE TABLE quiz_attempts (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               quiz_id BIGINT NOT NULL,
                               student_id BIGINT NOT NULL,
                               start_time DATETIME(6) NOT NULL,
                               submitted_at DATETIME(6),
                               total_marks INT NOT NULL,
                               obtained_marks INT NOT NULL DEFAULT 0,
                               percentage DOUBLE NOT NULL DEFAULT 0.0,
                               status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
                               auto_submitted BOOLEAN NOT NULL DEFAULT FALSE,
                               warning_count INT NOT NULL DEFAULT 0,
                               INDEX idx_attempt_quiz (quiz_id),
                               INDEX idx_attempt_student (student_id),
                               INDEX idx_attempt_status (status),
                               CONSTRAINT uk_quiz_student UNIQUE (quiz_id, student_id),
                               FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
                               FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Student Answers Table
CREATE TABLE student_answers (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 quiz_attempt_id BIGINT NOT NULL,
                                 question_id BIGINT NOT NULL,
                                 selected_option INT CHECK (selected_option BETWEEN 1 AND 4),
                                 is_correct BOOLEAN NOT NULL DEFAULT FALSE,
                                 attempted BOOLEAN NOT NULL DEFAULT FALSE,
                                 INDEX idx_sa_attempt (quiz_attempt_id),
                                 INDEX idx_sa_question (question_id),
                                 CONSTRAINT uk_attempt_question UNIQUE (quiz_attempt_id, question_id),
                                 FOREIGN KEY (quiz_attempt_id) REFERENCES quiz_attempts(id) ON DELETE CASCADE,
                                 FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- SEED DATA
-- ============================================

-- Insert Admin User (password: admin123 - BCrypt hash)
INSERT INTO users (username, password, full_name, email, role, active)
VALUES
    ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System Administrator', 'admin@quiz.com', 'ADMIN', TRUE),
    ('student1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'John Doe', 'john@quiz.com', 'STUDENT', TRUE),
    ('student2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Jane Smith', 'jane@quiz.com', 'STUDENT', TRUE);

-- Insert Sample Subjects
INSERT INTO subjects (name, description, created_by)
VALUES
    ('Java Programming', 'Core Java and Advanced Java concepts', 1),
    ('Database Management', 'SQL, MySQL, PostgreSQL fundamentals', 1),
    ('Data Structures', 'Arrays, LinkedLists, Trees, Graphs', 1);

-- Insert Sample Topics
INSERT INTO topics (name, description, subject_id)
VALUES
    ('OOP Concepts', 'Object-Oriented Programming in Java', 1),
    ('Collections Framework', 'List, Set, Map interfaces', 1),
    ('SQL Basics', 'SELECT, INSERT, UPDATE, DELETE queries', 2),
    ('Joins', 'INNER, LEFT, RIGHT, FULL OUTER joins', 2),
    ('Arrays', 'Array operations and algorithms', 3);

-- Insert Sample Questions
INSERT INTO questions (question_text, type, option1, option2, option3, option4, correct_option, subject_id, topic_id, marks)
VALUES
    ('What is the parent class of all Java classes?', 'OBJECTIVE', 'Object', 'Class', 'System', 'String', 1, 1, 1, 1),
    ('Which collection does not allow duplicates?', 'OBJECTIVE', 'List', 'Set', 'Map', 'Queue', 2, 1, 2, 1),
    ('What does SQL stand for?', 'OBJECTIVE', 'Structured Query Language', 'Simple Query Language', 'System Query Language', 'Standard Query Language', 1, 2, 3, 1),
    ('Which join returns all records from left table?', 'OBJECTIVE', 'INNER JOIN', 'LEFT JOIN', 'RIGHT JOIN', 'FULL JOIN', 2, 2, 4, 1),
    ('Time complexity of binary search?', 'OBJECTIVE', 'O(n)', 'O(log n)', 'O(n²)', 'O(1)', 2, 3, 5, 1);

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

-- Count records
SELECT 'users' AS table_name, COUNT(*) AS count FROM users
UNION ALL
SELECT 'subjects', COUNT(*) FROM subjects
UNION ALL
SELECT 'topics', COUNT(*) FROM topics
UNION ALL
SELECT 'questions', COUNT(*) FROM questions
UNION ALL
SELECT 'quizzes', COUNT(*) FROM quizzes
UNION ALL
SELECT 'quiz_attempts', COUNT(*) FROM quiz_attempts
UNION ALL
SELECT 'student_answers', COUNT(*) FROM student_answers;

-- View all indexes
SELECT
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    NON_UNIQUE
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = DATABASE()
ORDER BY TABLE_NAME, INDEX_NAME;

-- View all foreign keys
SELECT
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = DATABASE()
  AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME;