-- ============================================
-- StudentSys - Student Information Management
-- Database initialization script (MySQL 5.7)
-- ============================================

CREATE DATABASE IF NOT EXISTS student_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE student_db;

-- Drop in dependency order
DROP TABLE IF EXISTS forum_reply;
DROP TABLE IF EXISTS forum_post;
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS enrollment;
DROP TABLE IF EXISTS schedule_slot;
DROP TABLE IF EXISTS grade;
DROP TABLE IF EXISTS subject;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS class_info;
DROP TABLE IF EXISTS sys_user;

-- ----------------------------
-- Table: sys_user
-- ----------------------------
CREATE TABLE sys_user (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50)  DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table: class_info
-- ----------------------------
CREATE TABLE class_info (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    class_name  VARCHAR(100) NOT NULL,
    teacher     VARCHAR(50)  DEFAULT NULL,
    description VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table: student
-- ----------------------------
CREATE TABLE student (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    student_no VARCHAR(30)  NOT NULL UNIQUE,
    name       VARCHAR(50)  NOT NULL,
    gender     VARCHAR(10)  DEFAULT NULL,
    age        INT          DEFAULT NULL,
    phone      VARCHAR(20)  DEFAULT NULL,
    class_id   BIGINT       DEFAULT NULL,
    address    VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_class_id (class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table: subject
-- ----------------------------
CREATE TABLE subject (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    subject_name VARCHAR(100) NOT NULL,
    credit       DECIMAL(3,1) NOT NULL DEFAULT 3.0,
    description  VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table: grade
-- ----------------------------
CREATE TABLE grade (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    student_id BIGINT       NOT NULL,
    subject_id BIGINT       NOT NULL,
    score      DECIMAL(5,2) DEFAULT NULL,
    semester   VARCHAR(30)  DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_student_id (student_id),
    KEY idx_subject_id (subject_id),
    UNIQUE KEY uk_student_subject_semester (student_id, subject_id, semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table: schedule_slot
-- ----------------------------
CREATE TABLE schedule_slot (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    subject_id  BIGINT      NOT NULL,
    day_of_week TINYINT     NOT NULL COMMENT '1=Mon 2=Tue 3=Wed 4=Thu 5=Fri',
    start_time  TIME        NOT NULL,
    end_time    TIME        NOT NULL,
    room        VARCHAR(80) DEFAULT NULL,
    semester    VARCHAR(30) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_subject_id (subject_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table: enrollment
-- ----------------------------
CREATE TABLE enrollment (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    student_id BIGINT      NOT NULL,
    subject_id BIGINT      NOT NULL,
    semester   VARCHAR(30) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_subject_sem (student_id, subject_id, semester),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table: forum_post
-- ----------------------------
CREATE TABLE forum_post (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    title      VARCHAR(200) NOT NULL,
    content    TEXT,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table: forum_reply
-- ----------------------------
CREATE TABLE forum_reply (
    id         BIGINT   NOT NULL AUTO_INCREMENT,
    post_id    BIGINT   NOT NULL,
    user_id    BIGINT   NOT NULL,
    content    TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_post_id (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table: message
-- ----------------------------
CREATE TABLE message (
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    sender_id   BIGINT   NOT NULL,
    receiver_id BIGINT   NOT NULL,
    content     TEXT,
    is_read     TINYINT  NOT NULL DEFAULT 0,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_sender (sender_id),
    KEY idx_receiver (receiver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Mock data
-- ============================================

-- Users
INSERT INTO sys_user (username, password, nickname) VALUES
('admin',    'admin',  'Admin'),
('jmiller',  '123456', 'Prof. James Miller'),
('swilson',  '123456', 'Prof. Sarah Wilson');

-- Classes (US college cohorts)
INSERT INTO class_info (class_name, teacher, description) VALUES
('CS Freshman Cohort A',  'Prof. James Miller', 'Computer Science freshmen - Fall 2024 intake'),
('CS Freshman Cohort B',  'Prof. Sarah Wilson', 'Computer Science freshmen - Fall 2024 intake'),
('Business Sophomore A',  'Prof. David Park',   'Business Administration sophomores'),
('Engineering Junior A',  'Prof. Maria Garcia', 'Mechanical Engineering juniors');

-- Students (diverse US college names)
INSERT INTO student (student_no, name, gender, age, phone, class_id, address) VALUES
('U20240101', 'James Thompson',  'Male',   19, '(415) 555-0101', 1, '120 University Ave, Berkeley, CA'),
('U20240102', 'Emily Chen',      'Female', 18, '(415) 555-0102', 1, '245 College St, Berkeley, CA'),
('U20240103', 'Sofia Martinez',  'Female', 19, '(415) 555-0103', 1, '380 Campus Dr, Berkeley, CA'),
('U20240201', 'Liam Johnson',    'Male',   20, '(310) 555-0201', 2, '55 Westwood Blvd, Los Angeles, CA'),
('U20240202', 'Aisha Patel',     'Female', 19, '(310) 555-0202', 2, '102 Hilgard Ave, Los Angeles, CA'),
('U20240203', 'Carlos Rivera',   'Male',   18, '(310) 555-0203', 2, '78 Gayley Ave, Los Angeles, CA'),
('U20240301', 'Olivia Williams', 'Female', 20, '(617) 555-0301', 3, '33 Beacon St, Boston, MA'),
('U20240302', 'Noah Kim',        'Male',   21, '(617) 555-0302', 3, '440 Commonwealth Ave, Boston, MA'),
('U20240303', 'Maya Jackson',    'Female', 20, '(617) 555-0303', 3, '15 Bay State Rd, Boston, MA'),
('U20240401', 'Ethan Brown',     'Male',   21, '(734) 555-0401', 4, '925 S State St, Ann Arbor, MI'),
('U20240402', 'Priya Sharma',    'Female', 20, '(734) 555-0402', 4, '1200 S University Ave, Ann Arbor, MI'),
('U20240403', 'Tyler Davis',     'Male',   21, '(734) 555-0403', 4, '530 S Forest Ave, Ann Arbor, MI');

-- Subjects (US course codes)
INSERT INTO subject (subject_name, credit, description) VALUES
('MATH 201 - Calculus II',         4.0, 'Integration techniques, series, and multivariable intro'),
('ENGL 101 - College Writing',     3.0, 'Academic writing, research papers, and rhetoric'),
('PHYS 151 - General Physics I',   4.0, 'Mechanics, thermodynamics, and waves with lab'),
('CS 101 - Intro to Programming',  3.5, 'Fundamentals of programming using Python'),
('HIST 110 - US History',          3.0, 'American history from colonial era to present'),
('ECON 200 - Microeconomics',      3.0, 'Supply and demand, market structures, game theory');

-- Grades
INSERT INTO grade (student_id, subject_id, score, semester) VALUES
(1,1,92.5,'2024-Fall'),(1,2,85.0,'2024-Fall'),(1,3,88.0,'2024-Fall'),
(1,4,95.0,'2024-Fall'),(1,5,78.0,'2024-Fall'),(1,6,82.0,'2024-Fall'),
(2,1,88.0,'2024-Fall'),(2,2,91.5,'2024-Fall'),(2,3,85.0,'2024-Fall'),
(2,4,87.0,'2024-Fall'),(2,5,90.0,'2024-Fall'),(2,6,88.5,'2024-Fall'),
(3,1,75.0,'2024-Fall'),(3,2,92.0,'2024-Fall'),(3,3,70.5,'2024-Fall'),
(3,4,72.0,'2024-Fall'),(3,5,95.0,'2024-Fall'),(3,6,93.0,'2024-Fall'),
(4,1,95.0,'2024-Fall'),(4,2,78.0,'2024-Fall'),(4,3,93.5,'2024-Fall'),
(4,4,91.0,'2024-Fall'),(4,5,72.0,'2024-Fall'),(4,6,76.0,'2024-Fall'),
(5,1,82.0,'2024-Fall'),(5,2,80.5,'2024-Fall'),(5,3,79.0,'2024-Fall'),
(5,4,83.0,'2024-Fall'),(5,5,81.0,'2024-Fall'),(5,6,80.0,'2024-Fall'),
(6,1,96.0,'2024-Fall'),(6,2,94.0,'2024-Fall'),(6,3,92.5,'2024-Fall'),
(6,4,98.0,'2024-Fall'),(6,5,91.0,'2024-Fall'),(6,6,93.5,'2024-Fall'),
(7,1,80.0,'2024-Fall'),(7,2,95.5,'2024-Fall'),(7,3,76.0,'2024-Fall'),
(7,4,78.0,'2024-Fall'),(7,5,96.0,'2024-Fall'),(7,6,91.0,'2024-Fall'),
(8,1,90.0,'2024-Fall'),(8,2,74.0,'2024-Fall'),(8,3,91.5,'2024-Fall'),
(8,4,93.0,'2024-Fall'),(8,5,68.0,'2024-Fall'),(8,6,72.0,'2024-Fall'),
(9,1,86.0,'2024-Fall'),(9,2,88.0,'2024-Fall'),(9,3,84.0,'2024-Fall'),
(9,4,85.5,'2024-Fall'),(9,5,87.0,'2024-Fall'),(9,6,86.5,'2024-Fall'),
(10,1,68.0,'2024-Fall'),(10,2,72.0,'2024-Fall'),(10,3,65.5,'2024-Fall'),
(10,4,70.0,'2024-Fall'),(10,5,74.0,'2024-Fall'),(10,6,71.0,'2024-Fall'),
(11,1,91.0,'2024-Fall'),(11,2,89.5,'2024-Fall'),(11,3,87.0,'2024-Fall'),
(11,4,90.0,'2024-Fall'),(11,5,92.5,'2024-Fall'),(11,6,88.0,'2024-Fall'),
(12,1,87.0,'2024-Fall'),(12,2,76.0,'2024-Fall'),(12,3,89.5,'2024-Fall'),
(12,4,88.0,'2024-Fall'),(12,5,73.0,'2024-Fall'),(12,6,75.5,'2024-Fall');

-- Schedule slots (MWF and TTh patterns)
INSERT INTO schedule_slot (subject_id, day_of_week, start_time, end_time, room, semester) VALUES
-- MATH 201: MWF 9:00-9:50
(1, 1, '09:00', '09:50', 'Science Hall 204', '2024-Fall'),
(1, 3, '09:00', '09:50', 'Science Hall 204', '2024-Fall'),
(1, 5, '09:00', '09:50', 'Science Hall 204', '2024-Fall'),
-- ENGL 101: TTh 10:00-11:15
(2, 2, '10:00', '11:15', 'Humanities Bldg 110', '2024-Fall'),
(2, 4, '10:00', '11:15', 'Humanities Bldg 110', '2024-Fall'),
-- PHYS 151: MWF 11:00-11:50
(3, 1, '11:00', '11:50', 'Physics Lab 301', '2024-Fall'),
(3, 3, '11:00', '11:50', 'Physics Lab 301', '2024-Fall'),
(3, 5, '11:00', '11:50', 'Physics Lab 301', '2024-Fall'),
-- CS 101: TTh 13:00-14:15
(4, 2, '13:00', '14:15', 'Tech Center 105', '2024-Fall'),
(4, 4, '13:00', '14:15', 'Tech Center 105', '2024-Fall'),
-- HIST 110: MWF 14:00-14:50
(5, 1, '14:00', '14:50', 'Liberal Arts 220', '2024-Fall'),
(5, 3, '14:00', '14:50', 'Liberal Arts 220', '2024-Fall'),
(5, 5, '14:00', '14:50', 'Liberal Arts 220', '2024-Fall'),
-- ECON 200: TTh 15:30-16:45
(6, 2, '15:30', '16:45', 'Business School 402', '2024-Fall'),
(6, 4, '15:30', '16:45', 'Business School 402', '2024-Fall');

-- Enrollments (each student enrolled in 4-6 courses)
INSERT INTO enrollment (student_id, subject_id, semester) VALUES
(1,1,'2024-Fall'),(1,2,'2024-Fall'),(1,3,'2024-Fall'),(1,4,'2024-Fall'),
(2,1,'2024-Fall'),(2,2,'2024-Fall'),(2,4,'2024-Fall'),(2,5,'2024-Fall'),
(3,2,'2024-Fall'),(3,3,'2024-Fall'),(3,5,'2024-Fall'),(3,6,'2024-Fall'),
(4,1,'2024-Fall'),(4,3,'2024-Fall'),(4,4,'2024-Fall'),(4,6,'2024-Fall'),
(5,1,'2024-Fall'),(5,2,'2024-Fall'),(5,5,'2024-Fall'),(5,6,'2024-Fall'),
(6,1,'2024-Fall'),(6,2,'2024-Fall'),(6,3,'2024-Fall'),(6,4,'2024-Fall'),(6,5,'2024-Fall'),(6,6,'2024-Fall'),
(7,2,'2024-Fall'),(7,5,'2024-Fall'),(7,6,'2024-Fall'),
(8,1,'2024-Fall'),(8,3,'2024-Fall'),(8,4,'2024-Fall'),
(9,1,'2024-Fall'),(9,2,'2024-Fall'),(9,3,'2024-Fall'),(9,5,'2024-Fall'),
(10,1,'2024-Fall'),(10,3,'2024-Fall'),(10,4,'2024-Fall'),(10,6,'2024-Fall'),
(11,1,'2024-Fall'),(11,2,'2024-Fall'),(11,3,'2024-Fall'),(11,5,'2024-Fall'),(11,6,'2024-Fall'),
(12,1,'2024-Fall'),(12,3,'2024-Fall'),(12,4,'2024-Fall');

-- Forum posts
INSERT INTO forum_post (user_id, title, content, created_at) VALUES
(1, 'Welcome to the Fall 2024 Forum!',
 'Hey everyone! This is the official discussion board for Fall 2024. Feel free to post questions about courses, form study groups, or share resources. Keep it respectful!',
 '2024-08-25 10:00:00'),
(2, 'MATH 201 - Midterm Study Group',
 'Anyone interested in forming a study group for the MATH 201 midterm? We could meet at the library Tuesdays and Thursdays at 5 PM. Drop a reply if you''re in!',
 '2024-09-15 14:30:00'),
(3, 'CS 101 - Homework 3 Help',
 'I''m stuck on problem 4 of HW3 (the recursion problem). Has anyone figured out the base case? I keep getting infinite loops. No spoilers please, just hints!',
 '2024-09-20 09:15:00'),
(1, 'Campus Career Fair - Oct 15',
 'Reminder: The fall career fair is October 15th in the Student Union from 10 AM - 4 PM. Bring your resume! Companies attending include Google, Amazon, and several startups.',
 '2024-10-01 11:00:00'),
(2, 'Physics Lab Report Format',
 'Prof. Garcia mentioned a new format for PHYS 151 lab reports starting next week. Make sure you download the updated template from the course page before submitting.',
 '2024-10-05 16:45:00'),
(3, 'Looking for ECON 200 Textbook',
 'Does anyone have a used copy of "Microeconomics" by Perloff (8th edition)? The bookstore is sold out and I don''t want to pay full price online. Will pay $40.',
 '2024-10-08 13:20:00');

-- Forum replies
INSERT INTO forum_reply (post_id, user_id, content, created_at) VALUES
(1, 2, 'Thanks for setting this up! Looking forward to a great semester.', '2024-08-25 11:30:00'),
(1, 3, 'Great idea! Can we also use this for sharing notes?', '2024-08-25 14:00:00'),
(2, 1, 'I''m in! Tuesdays work better for me. See you at the library.', '2024-09-15 18:00:00'),
(2, 3, 'Count me in too. Should we book a study room?', '2024-09-16 09:00:00'),
(2, 2, 'Good idea! I''ll book Room 204 in the library for Tuesdays 5-7 PM.', '2024-09-16 10:30:00'),
(3, 2, 'Hint: think about what the smallest valid input is. Your base case should handle that.', '2024-09-20 11:00:00'),
(3, 1, 'Also check if you''re actually reducing the problem size in each recursive call!', '2024-09-20 12:45:00'),
(4, 3, 'Awesome, I''ll be there! Anyone want to prep together beforehand?', '2024-10-01 15:00:00'),
(5, 1, 'Got it, thanks for the heads up Prof. Wilson!', '2024-10-05 17:30:00'),
(6, 1, 'I have one! DM me and we can meet up on campus.', '2024-10-08 14:00:00');

-- Messages (conversations between users)
INSERT INTO message (sender_id, receiver_id, content, is_read, created_at) VALUES
(1, 2, 'Hey Prof. Miller, are office hours still 2-4 PM on Wednesdays?', 1, '2024-09-10 09:00:00'),
(2, 1, 'Yes, still Wed 2-4 PM in my office (Tech Center 310). Come by anytime!', 1, '2024-09-10 10:30:00'),
(1, 2, 'Perfect, I''ll stop by tomorrow to discuss my midterm grade. Thanks!', 1, '2024-09-10 11:00:00'),
(1, 3, 'Hey Sofia! Did you finish the CS 101 assignment yet?', 1, '2024-09-18 15:00:00'),
(3, 1, 'Almost done! The last function is tricky. Want to compare answers later?', 1, '2024-09-18 15:30:00'),
(1, 3, 'Sure, let''s meet at the coffee shop at 4?', 1, '2024-09-18 15:45:00'),
(3, 1, 'Sounds good, see you there!', 1, '2024-09-18 16:00:00'),
(2, 3, 'Hi Sofia, just a reminder your MATH 201 extra credit is due Friday.', 0, '2024-10-07 08:00:00'),
(3, 2, 'Thanks for the reminder! I''ll have it ready.', 0, '2024-10-07 09:15:00');
