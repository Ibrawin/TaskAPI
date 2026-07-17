# Task API

A RESTful API for managing tasks built with Spring Boot. Supports creating, reading, updating, and deleting tasks with full request validation and consistent error responses.

---

## Tech Stack

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.0 |
| Spring Data JPA | via Spring Boot |
| H2 Database | In-memory |
| MapStruct | 1.6.3 |
| Lombok | via Spring Boot |
| JUnit 5 | via Spring Boot |
| Mockito | via Spring Boot |

---

## How to Run

### Prerequisites
- Java 21
- Maven

### Steps

```bash
# Clone the repository
git clone <repo-url>
cd TaskAPI

# Run the application
./mvnw spring-boot:run
```

The application starts on **http://localhost:8080**

The H2 console is available at **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: *(leave blank)*

### Run the tests

```bash
./mvnw test
```

---

## API Endpoints

### Create a task
```
POST /api/tasks
```
**Request body:**
```json
{
  "title": "Clean the flat",
  "description": "Hoover and mop all rooms",
  "completed": false
}
```
**Response:** `201 Created`
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "title": "Clean the flat",
  "description": "Hoover and mop all rooms",
  "completed": false,
  "createdAt": "2026-07-14T10:30:00"
}
```

---

### Get all tasks
```
GET /api/tasks
```
**Response:** `200 OK` — array of task objects

---

### Get a task by ID
```
GET /api/tasks/{id}
```
**Response:** `200 OK` — single task object
**Error:** `404 Not Found` if the task does not exist

---

### Update a task
```
PUT /api/tasks/{id}
```
**Request body:** same shape as POST
**Response:** `200 OK` — updated task object
**Notes:** The original `id` and `createdAt` are always preserved regardless of what is sent in the request body
**Error:** `404 Not Found` if the task does not exist

---

### Delete a task
```
DELETE /api/tasks/{id}
```
**Response:** `204 No Content`
**Error:** `404 Not Found` if the task does not exist

---

## Validation

`title` is required and must not be blank. Sending an invalid request returns `400 Bad Request` with a response body describing the failing fields:

```json
{
  "timestamp": "2026-07-14T10:30:00",
  "status": 400,
  "message": "Validation failed",
  "fieldErrors": [
    "title: Title is required"
  ]
}
```

---

## Error Responses

All errors return a consistent `ApiError` structure:

```json
{
  "timestamp": "2026-07-14T10:30:00",
  "status": 404,
  "message": "Resource not found",
  "fieldErrors": []
}
```

| Status | Cause |
|---|---|
| 400 | Validation failure — missing or invalid fields |
| 404 | Task not found by the given ID |
| 500 | Unexpected server error |

---

## Project Structure

```
src/main/java/com/ibrawin/taskapi/
├── controller/
│   └── TaskController.java        # HTTP layer — routes and response codes
├── domain/
│   └── Task.java                  # JPA entity
├── dto/
│   ├── TaskRequest.java           # Inbound request shape
│   └── TaskResponse.java          # Outbound response shape
├── exceptions/
│   ├── ApiError.java              # Consistent error response structure
│   ├── GlobalExceptionHandler.java # Maps exceptions to HTTP responses
│   └── NotFoundException.java     # Thrown when a task is not found
├── mapper/
│   └── TaskMapper.java            # MapStruct interface — entity ↔ DTO conversion
├── repositories/
│   └── TaskRepository.java        # Spring Data JPA repository
└── services/
    ├── TaskService.java            # Service interface
    └── TaskServiceImpl.java        # Business logic implementation
```

---

## Architectural Decisions

**DTOs separate from the entity** — `TaskRequest` and `TaskResponse` are Java records kept completely separate from the `Task` JPA entity. This means the database schema and the API contract can evolve independently, and implementation details like column names are never exposed to API consumers.

**MapStruct for mapping** — MapStruct generates mapping code at compile time rather than using reflection at runtime. This gives type safety, compile-time errors if fields do not match, and better performance than manual or reflection-based mappers.

**Service interface and implementation** — `TaskService` defines the contract, `TaskServiceImpl` provides the behaviour. The controller depends only on the interface, making it straightforward to swap implementations or mock the service in tests without changing the controller.

**@Transactional at service level** — `@Transactional(readOnly = true)` is set at the class level as the default for all methods. Write methods (`saveTask`, `updateTaskById`, `deleteTaskById`) override this with `@Transactional` to ensure multiple database operations within a single method succeed or fail together.

**Constructor injection via Lombok** — all dependencies are injected via constructor using `@RequiredArgsConstructor`. This makes dependencies explicit, keeps fields immutable, and allows the class to be instantiated and tested without a Spring context.
