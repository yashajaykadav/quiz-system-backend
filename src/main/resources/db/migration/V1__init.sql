-- ============================================================
-- V1__init.sql — Schema aligned with JPA entity definitions
-- ============================================================
-- NOTE: This migration is baselined (already applied).
-- Flyway treats this as "already done" and starts from V2.
-- Do NOT add DROP TABLE or SELECT statements here.
-- ============================================================

-- users  →  User.java
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    username    VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL,
    active      BIT(1)       NOT NULL DEFAULT 1,
    created_at  DATETIME(6)  NOT NULL,
    last_login  DATETIME(6)  DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY UK_users_email    (email),
    UNIQUE KEY UK_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- subjects  →  Subject.java
CREATE TABLE IF NOT EXISTS subjects (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description TEXT         DEFAULT NULL,
    created_at  DATETIME(6)  NOT NULL,
    created_by  BIGINT       DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY UK_subjects_name (name),
    KEY FK_subjects_created_by (created_by),
    CONSTRAINT FK_subjects_created_by FOREIGN KEY (created_by) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- topics  →  Topic.java
CREATE TABLE IF NOT EXISTS topics (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description TEXT         DEFAULT NULL,
    subject_id  BIGINT       NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_topic_name_subject UNIQUE (name, subject_id),
    KEY FK_topics_subject (subject_id),
    CONSTRAINT FK_topics_subject FOREIGN KEY (subject_id) REFERENCES subjects (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- questions  →  Question.java
CREATE TABLE IF NOT EXISTS questions (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    question_text   TEXT         NOT NULL,
    code_snippet    TEXT         DEFAULT NULL,
    type            VARCHAR(20)  NOT NULL,
    option1         VARCHAR(500) NOT NULL,
    option2         VARCHAR(500) NOT NULL,
    option3         VARCHAR(500) NOT NULL,
    option4         VARCHAR(500) NOT NULL,
    correct_option  INT          NOT NULL,
    subject_id      BIGINT       NOT NULL,
    topic_id        BIGINT       NOT NULL,
    marks           INT          NOT NULL DEFAULT 1,
    created_at      DATETIME(6)  NOT NULL,
    updated_at      DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    KEY FK_questions_subject (subject_id),
    KEY FK_questions_topic   (topic_id),
    CONSTRAINT FK_questions_subject FOREIGN KEY (subject_id) REFERENCES subjects  (id),
    CONSTRAINT FK_questions_topic   FOREIGN KEY (topic_id)   REFERENCES topics    (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- quizzes  →  Quiz.java
CREATE TABLE IF NOT EXISTS quizzes (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    title            VARCHAR(255) NOT NULL,
    description      TEXT         DEFAULT NULL,
    subject_id       BIGINT       NOT NULL,
    topic_id         BIGINT       NOT NULL,
    duration_minutes INT          NOT NULL,
    scheduled_date   DATETIME(6)  NOT NULL,
    total_marks      INT          NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'SCHEDULED',
    created_by       BIGINT       DEFAULT NULL,
    created_at       DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    KEY idx_quiz_subject        (subject_id),
    KEY idx_quiz_topic          (topic_id),
    KEY idx_quiz_status         (status),
    KEY idx_quiz_scheduled_date (scheduled_date),
    KEY FK_quizzes_created_by   (created_by),
    CONSTRAINT FK_quizzes_subject    FOREIGN KEY (subject_id) REFERENCES subjects (id),
    CONSTRAINT FK_quizzes_topic      FOREIGN KEY (topic_id)   REFERENCES topics   (id),
    CONSTRAINT FK_quizzes_created_by FOREIGN KEY (created_by) REFERENCES users    (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- quiz_questions  →  Quiz.java @ManyToMany @JoinTable
CREATE TABLE IF NOT EXISTS quiz_questions (
    quiz_id     BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    PRIMARY KEY (quiz_id, question_id),
    KEY FK_qq_question (question_id),
    CONSTRAINT FK_qq_quiz      FOREIGN KEY (quiz_id)     REFERENCES quizzes   (id),
    CONSTRAINT FK_qq_question  FOREIGN KEY (question_id) REFERENCES questions (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- quiz_attempts  →  QuizAttempt.java
CREATE TABLE IF NOT EXISTS quiz_attempts (
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    quiz_id        BIGINT      NOT NULL,
    student_id     BIGINT      NOT NULL,
    start_time     DATETIME(6) NOT NULL,
    submitted_at   DATETIME(6) DEFAULT NULL,
    total_marks    INT         NOT NULL,
    obtained_marks INT         NOT NULL DEFAULT 0,
    percentage     DOUBLE      NOT NULL DEFAULT 0.0,
    status         VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    auto_submitted BIT(1)      NOT NULL DEFAULT 0,
    warning_count  INT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_attempt_quiz    (quiz_id),
    KEY idx_attempt_student (student_id),
    KEY idx_attempt_status  (status),
    CONSTRAINT FK_attempts_quiz    FOREIGN KEY (quiz_id)    REFERENCES quizzes (id),
    CONSTRAINT FK_attempts_student FOREIGN KEY (student_id) REFERENCES users   (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- student_answers  →  StudentAnswer.java
CREATE TABLE IF NOT EXISTS student_answers (
    id              BIGINT  NOT NULL AUTO_INCREMENT,
    quiz_attempt_id BIGINT  NOT NULL,
    question_id     BIGINT  NOT NULL,
    selected_option INT     DEFAULT NULL,
    is_correct      BIT(1)  NOT NULL DEFAULT 0,
    attempted       BIT(1)  NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY FK_sa_attempt  (quiz_attempt_id),
    KEY FK_sa_question (question_id),
    CONSTRAINT FK_sa_attempt  FOREIGN KEY (quiz_attempt_id) REFERENCES quiz_attempts (id),
    CONSTRAINT FK_sa_question FOREIGN KEY (question_id)     REFERENCES questions     (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;