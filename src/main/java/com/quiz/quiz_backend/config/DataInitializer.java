package com.quiz.quiz_backend.config;

import com.quiz.quiz_backend.entity.*;
import com.quiz.quiz_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeUsers();
        initializeSubjects();
        initializeTopics();
        initializeQuestions();
        initializeQuizzes();
    }

    private void initializeUsers() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .email("admin@quiz.com")
                    .role(Role.ADMIN)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user created: admin / admin123");
        }

        if (!userRepository.existsByUsername("student1")) {
            User student1 = User.builder()
                    .username("student1")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("John Doe")
                    .email("john@quiz.com")
                    .role(Role.STUDENT)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(student1);
            System.out.println("Student created: student1 / admin123");
        }

        if (!userRepository.existsByUsername("student2")) {
            User student2 = User.builder()
                    .username("student2")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Jane Smith")
                    .email("jane@quiz.com")
                    .role(Role.STUDENT)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(student2);
            System.out.println("Student created: student2 / admin123");
        }
    }

    private void initializeSubjects() {
        if (subjectRepository.count() == 0) {
            // Set createdBy to the admin user so subjects are not orphaned
            User admin = userRepository.findByUsername("admin").orElse(null);

            List<Subject> subjects = new ArrayList<>();
            subjects.add(Subject.builder()
                    .name("Java Programming")
                    .description("Core Java and Advanced Java concepts")
                    .createdBy(admin)
                    .build());
            subjects.add(Subject.builder()
                    .name("Database Management")
                    .description("SQL, MySQL, PostgreSQL fundamentals")
                    .createdBy(admin)
                    .build());
            subjects.add(Subject.builder()
                    .name("Data Structures")
                    .description("Arrays, LinkedLists, Trees, Graphs")
                    .createdBy(admin)
                    .build());
            subjectRepository.saveAll(subjects);
            System.out.println("Subjects seeded: Java Programming, Database Management, Data Structures");
        }
    }

    private void initializeTopics() {
        if (topicRepository.count() == 0) {
            Subject javaSubject = subjectRepository.findByName("Java Programming").orElse(null);
            Subject dbSubject = subjectRepository.findByName("Database Management").orElse(null);
            Subject dsSubject = subjectRepository.findByName("Data Structures").orElse(null);

            if (javaSubject != null && dbSubject != null && dsSubject != null) {
                List<Topic> topics = List.of(
                    Topic.builder()
                            .name("OOP Concepts")
                            .description("Object-Oriented Programming in Java")
                            .subject(javaSubject)
                            .createdAt(LocalDateTime.now())
                            .build(),
                    Topic.builder()
                            .name("Collections Framework")
                            .description("List, Set, Map interfaces")
                            .subject(javaSubject)
                            .createdAt(LocalDateTime.now())
                            .build(),
                    Topic.builder()
                            .name("SQL Basics")
                            .description("SELECT, INSERT, UPDATE, DELETE queries")
                            .subject(dbSubject)
                            .createdAt(LocalDateTime.now())
                            .build(),
                    Topic.builder()
                            .name("Joins")
                            .description("INNER, LEFT, RIGHT, FULL OUTER joins")
                            .subject(dbSubject)
                            .createdAt(LocalDateTime.now())
                            .build(),
                    Topic.builder()
                            .name("Arrays")
                            .description("Array operations and algorithms")
                            .subject(dsSubject)
                            .createdAt(LocalDateTime.now())
                            .build()
                );
                topicRepository.saveAll(topics);
                System.out.println("Topics created: OOP Concepts, Collections Framework, SQL Basics, Joins, Arrays");
            }
        }
    }

    private void initializeQuestions() {
        if (questionRepository.count() == 0) {
            Subject javaSubject = subjectRepository.findByName("Java Programming").orElse(null);
            Subject dbSubject = subjectRepository.findByName("Database Management").orElse(null);
            Subject dsSubject = subjectRepository.findByName("Data Structures").orElse(null);

            if (javaSubject != null && dbSubject != null && dsSubject != null) {
                Topic oopTopic = topicRepository.findByNameAndSubjectId("OOP Concepts", javaSubject.getId()).orElse(null);
                Topic collectionsTopic = topicRepository.findByNameAndSubjectId("Collections Framework", javaSubject.getId()).orElse(null);
                Topic sqlTopic = topicRepository.findByNameAndSubjectId("SQL Basics", dbSubject.getId()).orElse(null);
                Topic joinsTopic = topicRepository.findByNameAndSubjectId("Joins", dbSubject.getId()).orElse(null);
                Topic arraysTopic = topicRepository.findByNameAndSubjectId("Arrays", dsSubject.getId()).orElse(null);

                List<Question> questions = new ArrayList<>();
                if (oopTopic != null) {
                    questions.add(Question.builder()
                            .questionText("What is the parent class of all Java classes?")
                            .type(QuestionType.OBJECTIVE)
                            .option1("Object")
                            .option2("Class")
                            .option3("System")
                            .option4("String")
                            .correctOption(1)
                            .subject(javaSubject)
                            .topic(oopTopic)
                            .marks(1)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build());
                }
                if (collectionsTopic != null) {
                    questions.add(Question.builder()
                            .questionText("Which collection does not allow duplicates?")
                            .type(QuestionType.OBJECTIVE)
                            .option1("List")
                            .option2("Set")
                            .option3("Map")
                            .option4("Queue")
                            .correctOption(2)
                            .subject(javaSubject)
                            .topic(collectionsTopic)
                            .marks(1)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build());
                }
                if (sqlTopic != null) {
                    questions.add(Question.builder()
                            .questionText("What does SQL stand for?")
                            .type(QuestionType.OBJECTIVE)
                            .option1("Structured Query Language")
                            .option2("Simple Query Language")
                            .option3("System Query Language")
                            .option4("Standard Query Language")
                            .correctOption(1)
                            .subject(dbSubject)
                            .topic(sqlTopic)
                            .marks(1)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build());
                }
                if (joinsTopic != null) {
                    questions.add(Question.builder()
                            .questionText("Which join returns all records from left table?")
                            .type(QuestionType.OBJECTIVE)
                            .option1("INNER JOIN")
                            .option2("LEFT JOIN")
                            .option3("RIGHT JOIN")
                            .option4("FULL JOIN")
                            .correctOption(2)
                            .subject(dbSubject)
                            .topic(joinsTopic)
                            .marks(1)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build());
                }
                if (arraysTopic != null) {
                    questions.add(Question.builder()
                            .questionText("Time complexity of binary search?")
                            .type(QuestionType.OBJECTIVE)
                            .option1("O(n)")
                            .option2("O(log n)")
                            .option3("O(n²)")
                            .option4("O(1)")
                            .correctOption(2)
                            .subject(dsSubject)
                            .topic(arraysTopic)
                            .marks(1)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build());
                }
                questionRepository.saveAll(questions);
                System.out.println("Questions created: 5 seeded questions");
            }
        }
    }

    private void initializeQuizzes() {
        if (quizRepository.count() == 0) {
            Subject javaSubject = subjectRepository.findByName("Java Programming").orElse(null);
            Topic oopTopic = javaSubject != null ? topicRepository.findByNameAndSubjectId("OOP Concepts", javaSubject.getId()).orElse(null) : null;
            User admin = userRepository.findByUsername("admin").orElse(null);

            if (javaSubject != null && oopTopic != null && admin != null) {
                // Get all questions for this topic
                List<Question> topicQuestions = questionRepository.findBySubjectIdAndTopicId(javaSubject.getId(), oopTopic.getId());
                
                if (!topicQuestions.isEmpty()) {
                    int totalMarks = topicQuestions.stream().mapToInt(q -> q.getMarks() != null ? q.getMarks() : 1).sum();

                    Quiz javaQuiz = Quiz.builder()
                            .title("Java OOP Basics Quiz")
                            .description("Test your knowledge on Object-Oriented Programming principles.")
                            .subject(javaSubject)
                            .topic(oopTopic)
                            .questions(topicQuestions)
                            .durationMinutes(15)
                            // Schedule it slightly in the past so it's currently active!
                            .scheduledDate(LocalDateTime.now().minusMinutes(5)) 
                            .totalMarks(totalMarks)
                            .status(QuizStatus.SCHEDULED)
                            .createdBy(admin)
                            .build();

                    quizRepository.save(javaQuiz);
                    System.out.println("Quiz created: Java OOP Basics Quiz (Active now)");
                }
            }
        }
    }
}
