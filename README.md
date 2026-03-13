# StudentSys -- Student Information Management System

A full-stack student information management system built with **Vue 3** + **Spring Boot 4** + **MySQL**. Designed for US college environments to manage students, classes, courses, grades, schedules, and campus communication.

---

## Tech Stack

| Layer    | Technology                        |
|----------|-----------------------------------|
| Frontend | Vue 3, Vite, Element Plus, Axios  |
| Backend  | Spring Boot 4, MyBatis, Maven     |
| Database | MySQL 5.7+                        |

---

## Features

### Dashboard
- Overview statistics: total students, classes, subjects, average score
- Grade distribution bar chart (A/B/C/D/F breakdown)
- Top 5 students leaderboard ranked by weighted GPA
- Average score per subject visualization

### Student Management
- Full CRUD operations with form validation
- Search by student name or filter by class
- Pagination with configurable page size

### Class Management
- Full CRUD for class/cohort information
- Track homeroom teacher and description
- Class dropdown integration across the system

### Subject Management
- Manage course catalog with credit hours (e.g. CS 101, MATH 201)
- Color-coded subject list
- Used as reference data for grade entry and scheduling

### Grade & GPA Management
- Record scores per student, subject, and semester
- Color-coded score badges (A/B/C/D/F)
- Per-student weighted GPA calculation (score x credits / total credits)
- Filter by student, subject, or semester

### Discussion Forum
- Card-based post list with search, pagination, and reply counts
- Full post detail view with threaded replies
- Create, edit, and delete posts and replies
- Author avatars and relative timestamps

### Direct Messages
- iMessage-style conversation sidebar with unread badges
- Real-time chat thread with sent/received bubble layout
- Start new conversations with any system user
- Unread count badge in sidebar navigation (auto-refreshes)

### Weekly Schedule
- Visual timetable grid: Monday-Friday, 8 AM - 6 PM
- Color-coded course blocks positioned by day and time
- Semester selector dropdown
- Add, edit, and delete schedule slots

### Authentication
- Session-based login with interceptor protection
- Unified JSON response format across all APIs

---

## Project Structure

```
Student_INFO_Management_System/
├── client/                         # Vue 3 Frontend
│   ├── src/
│   │   ├── api/                    # Axios API modules
│   │   │   ├── request.js          # Axios instance + interceptors
│   │   │   ├── user.js             # Auth APIs
│   │   │   ├── student.js          # Student CRUD
│   │   │   ├── classInfo.js        # Class CRUD
│   │   │   ├── subject.js          # Subject CRUD
│   │   │   ├── grade.js            # Grade CRUD + GPA
│   │   │   ├── dashboard.js        # Dashboard stats
│   │   │   ├── forum.js            # Forum posts & replies
│   │   │   ├── message.js          # Direct messages
│   │   │   └── schedule.js         # Schedule slots
│   │   ├── router/                 # Vue Router config
│   │   ├── views/                  # Page components
│   │   │   ├── Login.vue
│   │   │   ├── Layout.vue
│   │   │   ├── Dashboard.vue
│   │   │   ├── StudentList.vue
│   │   │   ├── ClassList.vue
│   │   │   ├── SubjectList.vue
│   │   │   ├── GradeList.vue
│   │   │   ├── Forum.vue           # Forum post list
│   │   │   ├── ForumPost.vue       # Post detail + replies
│   │   │   ├── Messages.vue        # DM conversations
│   │   │   └── Schedule.vue        # Weekly timetable
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
├── server/server/                  # Spring Boot Backend
│   ├── src/main/java/com/java/
│   │   ├── common/                 # Result + PageResult wrappers
│   │   ├── config/                 # CORS & Web config
│   │   ├── controller/             # REST controllers
│   │   ├── entity/                 # Data models
│   │   ├── interceptor/            # Login interceptor
│   │   ├── mapper/                 # MyBatis mapper interfaces
│   │   └── service/                # Business logic
│   ├── src/main/resources/
│   │   ├── mapper/                 # MyBatis XML mappers
│   │   └── application.properties
│   └── pom.xml
└── sql/
    └── init.sql                    # Database, tables & seed data
```

---

## Getting Started

### Prerequisites

- **JDK 17+**
- **Node.js 18+**
- **MySQL 5.7+**
- **Maven 3.9+** (or use the included `mvnw` wrapper)

### 1. Database Setup

Run the SQL initialization script in your MySQL client:

```bash
mysql -u root -p < sql/init.sql
```

This creates the `student_db` database with 10 tables and sample data including:
- 3 users, 4 classes, 12 students, 6 subjects, 72 grade records
- 15 schedule slots, 48 enrollments, 6 forum posts, 10 replies, 9 messages

### 2. Backend

```bash
cd server/server

# Update src/main/resources/application.properties with your MySQL credentials if needed

./mvnw spring-boot:run
```

The backend starts on **http://localhost:8080**.

### 3. Frontend

```bash
cd client

npm install
npm run dev
```

The frontend starts on **http://localhost:5173** and proxies API requests to the backend.

### 4. Login

Open **http://localhost:5173** and sign in:

| Username   | Password |
|------------|----------|
| admin      | admin    |
| jmiller    | 123456   |
| swilson    | 123456   |

---

## API Endpoints

### Authentication
| Method | Path           | Description    |
|--------|----------------|----------------|
| POST   | /api/login     | Login          |
| POST   | /api/logout    | Logout         |
| GET    | /api/userinfo  | Current user   |

### Dashboard
| Method | Path           | Description           |
|--------|----------------|-----------------------|
| GET    | /api/dashboard | Stats & analytics     |

### Students
| Method | Path                | Description              |
|--------|---------------------|--------------------------|
| GET    | /api/students       | List (search + paginate) |
| GET    | /api/students/:id   | Get by ID                |
| POST   | /api/students       | Create                   |
| PUT    | /api/students/:id   | Update                   |
| DELETE | /api/students/:id   | Delete                   |

### Classes
| Method | Path               | Description              |
|--------|--------------------|--------------------------|
| GET    | /api/classes       | List (search + paginate) |
| GET    | /api/classes/all   | All (for dropdowns)      |
| GET    | /api/classes/:id   | Get by ID                |
| POST   | /api/classes       | Create                   |
| PUT    | /api/classes/:id   | Update                   |
| DELETE | /api/classes/:id   | Delete                   |

### Subjects
| Method | Path                | Description              |
|--------|---------------------|--------------------------|
| GET    | /api/subjects       | List (search + paginate) |
| GET    | /api/subjects/all   | All (for dropdowns)      |
| GET    | /api/subjects/:id   | Get by ID                |
| POST   | /api/subjects       | Create                   |
| PUT    | /api/subjects/:id   | Update                   |
| DELETE | /api/subjects/:id   | Delete                   |

### Grades
| Method | Path                    | Description              |
|--------|-------------------------|--------------------------|
| GET    | /api/grades             | List (filter + paginate) |
| GET    | /api/grades/:id         | Get by ID                |
| POST   | /api/grades             | Create                   |
| PUT    | /api/grades/:id         | Update                   |
| DELETE | /api/grades/:id         | Delete                   |
| GET    | /api/grades/gpa/:sid    | Student weighted GPA     |
| GET    | /api/grades/semesters   | All semesters            |

### Forum
| Method | Path                         | Description           |
|--------|------------------------------|-----------------------|
| GET    | /api/forum                   | List posts (paginate) |
| GET    | /api/forum/:id               | Post + replies        |
| POST   | /api/forum                   | Create post           |
| PUT    | /api/forum/:id               | Edit post             |
| DELETE | /api/forum/:id               | Delete post           |
| POST   | /api/forum/:id/replies       | Add reply             |
| DELETE | /api/forum/replies/:replyId  | Delete reply          |

### Messages
| Method | Path                           | Description              |
|--------|--------------------------------|--------------------------|
| GET    | /api/messages/conversations    | User's conversations     |
| GET    | /api/messages/:userId          | Messages with a user     |
| POST   | /api/messages                  | Send message             |
| PUT    | /api/messages/read/:userId     | Mark conversation read   |
| GET    | /api/messages/unread-count     | Unread badge count       |
| GET    | /api/messages/users            | All users (new message)  |

### Schedule
| Method | Path                           | Description              |
|--------|--------------------------------|--------------------------|
| GET    | /api/schedule                  | All slots (by semester)  |
| GET    | /api/schedule/student/:sid     | Student's enrolled slots |
| POST   | /api/schedule                  | Add slot                 |
| PUT    | /api/schedule/:id              | Edit slot                |
| DELETE | /api/schedule/:id              | Delete slot              |
| GET    | /api/schedule/semesters        | Available semesters      |
| GET    | /api/schedule/enrollments      | Student enrollments      |
| POST   | /api/schedule/enrollments      | Enroll student           |
| DELETE | /api/schedule/enrollments/:id  | Drop enrollment          |

---

## License

This project is licensed under the MIT License.
