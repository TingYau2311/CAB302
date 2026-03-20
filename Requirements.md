# Tasktopia - Requirements Document
## Personal To-Do List Application with Categories
**Team Brainstormers**

---

## 1. Functional Requirements
*What the system must do*

### 1.1 Task Management
| ID | Requirement | Priority | Notes |
|----|-------------|----------|-------|
| F1.1 | Users shall create new tasks with a title and description | High | Core functionality |
| F1.2 | Users shall edit existing tasks | High | |
| F1.3 | Users shall delete tasks | High | |
| F1.4 | Users shall mark tasks as complete/incomplete | High | Visual indicator of completion |
| F1.5 | Users shall view all tasks in a list | High | |

### 1.2 Categories
| ID | Requirement | Priority | Notes |
|----|-------------|----------|-------|
| F2.1 | Users shall create custom categories (e.g., Work, Personal, Shopping) | High | |
| F2.2 | Users shall assign tasks to one or more categories | High | |
| F2.3 | Users shall view tasks filtered by category | High | |
| F2.4 | Users shall edit/delete categories | Medium | Consider if tasks in deleted category need reassignment |

### 1.3 Due Dates
| ID | Requirement | Priority | Notes |
|----|-------------|----------|-------|
| F3.1 | Users shall add due dates to tasks | Medium | |
| F3.2 | Users shall view tasks sorted by due date | Medium | |
| F3.3 | System shall highlight overdue tasks | Medium | e.g., with colour coding |

### 1.4 Data Persistence
| ID | Requirement | Priority | Notes |
|----|-------------|----------|-------|
| F4.1 | System shall save tasks to a file when the program exits | High | Prevent data loss |
| F4.2 | System shall load tasks from file when program starts | High | |
| F4.3 | System shall auto-save after each task change | Medium | Optional enhancement |

---

## 2. Non-Functional Requirements
*How well the system performs*

| ID | Requirement | Category | Target |
|----|-------------|----------|--------|
| NF1 | Application shall start within 3 seconds | Performance | Measurable |
| NF2 | Interface shall be intuitive for first-time users | Usability | Include simple menu prompts |
| NF3 | Task data shall not be lost on unexpected shutdown | Reliability | Save frequently |
| NF4 | Code shall follow Java naming conventions | Maintainability | |
| NF5 | Application shall handle up to 100 tasks without slowdown | Scalability | |

---

## 3. User Stories
*From the user's perspective*

