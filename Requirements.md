# Tasktopia - Requirements Document
## Personal To-Do List Application with Categories
**Team Brainstormers**

---

## 1. Functional Requirements
*What the system must do*

### 1.1 Task Management
| ID | Requirement | Priority | Notes |
|----|-------------|----------|-------|
| F1.1 | Users shall add new tasks | High | Core functionality |
| F1.2 | Users shall delete tasks | High | |
| F1.3 | Users shall check off completed tasks | High | Satisfaction tracking |
| F1.4 | Users shall edit existing tasks | High | |
| F1.5 | Users shall view tasks in a to-do list style format | High | Clean, readable display |

### 1.2 Categories
| ID | Requirement | Priority | Notes |
|----|-------------|----------|-------|
| F2.1 | Users shall create custom categories (e.g., Work, Personal, Shopping) | High | |
| F2.2 | Users shall assign tasks to categories | High | |
| F2.3 | Users shall filter tasks by category | High | |
| F2.4 | Users shall edit and delete categories | Medium | Consider task reassignment when deleting |

### 1.3 Due Dates & Urgency
| ID | Requirement | Priority | Notes |
|----|-------------|----------|-------|
| F3.1 | Users shall add due dates to tasks | Medium | |
| F3.2 | Users shall view tasks sorted by due date | Medium | |
| F3.3 | System shall highlight overdue tasks with colour coding | Medium | Different colours for: a few days overdue, one week overdue, very overdue |
| F3.4 | System shall provide notifications alerting users one week before a task is due | Medium | Console notification or pop-up |
| F3.5 | Users shall filter tasks by keywords | Medium | Search functionality |

### 1.4 Data Persistence
| ID | Requirement | Priority | Notes |
|----|-------------|----------|-------|
| F4.1 | System shall save tasks to a file when the program exits | High | Prevent data loss |
| F4.2 | System shall load tasks from file when program starts | High | |
| F4.3 | System shall auto-save after each task change | High | |

### 1.5 User Assistance
| ID | Requirement | Priority | Notes |
|----|-------------|----------|-------|
| F5.1 | System shall provide a help button/command for users needing assistance | Medium | Display instructions and tips |

---

## 2. Non-Functional Requirements
*How well the system performs*

| ID | Requirement | Category | Target |
|----|-------------|----------|--------|
| NF1 | Application shall start within 3 seconds | Performance | Measurable |
| NF2 | Interface shall be intuitive for first-time users | Usability | Clear menu prompts |
| NF3 | Task data shall not be lost on unexpected shutdown | Reliability | Frequent saves |
| NF4 | Code shall follow Java naming conventions | Maintainability | |
| NF5 | Application shall handle up to 100 tasks without slowdown | Scalability | |
| NF6 | Users shall be able to reorganise tasks based on urgency | Usability | Drag-and-drop or priority sorting |
| NF7 | Interface shall be user-friendly with clear navigation | Usability | |
| NF8 | Application shall be accessible (larger text option for vision impairments) | Accessibility | Future enhancement |
| NF9 | Users shall be able to customise interface appearance (colours, fonts) | Usability | Nice-to-have |

---

## 3. Future Enhancements (Scope Creep - Discuss with Team)
*Features to consider if time permits*

| ID | Feature | Notes |
|----|---------|-------|
| E1 | Team collaboration feature | Multiple users sharing tasks |
| E2 | Linked calendar integration | Connect with external calendars |
| E3 | Audio descriptions for hearing impairments | Accessibility enhancement |
| E4 | Advanced interface customisation | Full theme support |

---

## 4. User Stories
*To be completed during team brainstorming session*

*Example format:*
