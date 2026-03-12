## Car Rental Hub – Spring Boot REST API

This project is a Spring Boot REST API for managing a car rental platform. It was built as a practical examination to demonstrate Spring Data JPA relationships, pagination and sorting, query methods, layered architecture, and clean coding practices.

The core business domains are:
- **Locations**: hierarchical administrative areas (Province → District → Sector → Cell → Village)
- **Users & Roles**: application users attached to a village and assigned roles
- **Vehicles & Features**: rentable vehicles and their features
- **Customers, Reservations & Payments**: rental bookings and their payments

Entity relationship details are described in the ERD file: `CarRentalHub_ERD.drawio.pdf`.

---

### 1. Location Hierarchy & Saving (Requirement #2)

- **Entity**: `Location` (UUID id, code, name, type, parent, children)
- **Hierarchy**: `ELocationType` enum defines `PROVINCE`, `DISTRICT`, `SECTOR`, `CELL`, `VILLAGE`.
- **Mapping**:
  - `@ManyToOne` self-reference via `parent`
  - `@OneToMany(mappedBy = "parent")` `children`
- **Service**: `LocationService.saveChildAndParent(Location location, String parentId)`
  - Validates uniqueness of `code`
  - If `parentId` is present, loads parent and validates type transitions:
    - `PROVINCE → DISTRICT → SECTOR → CELL → VILLAGE`
  - If there is **no parent**, only `PROVINCE` is allowed.
  - Persists data via `LocationRepository` so the parent-child relationship is stored through the `parent_id` foreign key.
- **Controller**: `POST /api/locations/save`
  - Body: JSON `Location`
  - Optional query parameter `parentId` to attach as child.

This ensures saving any level of location (Province through Village) correctly maintains the hierarchy in the database.

---

### 2. Sorting & Pagination (Requirement #3)

The project demonstrates sorting and pagination in multiple endpoints:

- **Locations**
  - `GET /api/locations?page=0&size=10&sortBy=name&direction=ASC`
  - Uses `PageRequest.of(page, size, Sort.by(sortBy).ascending()/descending())`
  - Returns a `Page<Location>` plus metadata (currentPage, totalPages, totalItems, pageSize, sortBy, sortDirection).

- **Users**
  - `GET /api/users?page=0&size=10&sortBy=username&direction=ASC`
  - Uses `Pageable` to return `Page<UserDTO>` and the same pagination metadata.

- **Demo Controller**
  - `GET /api/demo/users/paged`
  - `GET /api/demo/vehicles/paged`
  - `GET /api/demo/reservations/by-status`
  - Explicitly documents how `Pageable` and `Sort` are used and how this translates to SQL `LIMIT/OFFSET` and `ORDER BY`.

**Performance Explanation**:
- Pagination keeps responses small (page-size items instead of all rows).
- Sorting is performed in the database with indexes, reducing in-memory work.
- Less data is sent over the network and processed by the server, improving response times and scalability.

---

### 3. Relationships (Requirements #4, #5, #6)

#### Many‑to‑Many with Join Table (Requirement #4)

- **Entities**: `Role` and `Permission`
- **Mapping**:
  - In `Role`:
    - `@ManyToMany`
    - `@JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))`
  - In `Permission`:
    - `@ManyToMany(mappedBy = "permissions")`
- This creates a **join table** `role_permissions(role_id, permission_id)` implementing a Many‑to‑Many relationship between roles and permissions.

The project also contains a Many‑to‑Many domain modeled with an explicit join entity:
- `Vehicle` ← `@OneToMany` → `VehicleFeature` ← `@ManyToOne` → `Feature`
- `VehicleFeature` holds extra attribute `additionalCost`, representing a Many‑to‑Many relationship between `Vehicle` and `Feature` with additional data.

#### One‑to‑Many & Many‑to‑One (Requirement #5)

- `Customer` ↔ `Reservation`
  - `Customer`:
    - `@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)`
  - `Reservation`:
    - `@ManyToOne`
    - `@JoinColumn(name = "customer_id")` (foreign key in `reservation` table)

- `Vehicle` ↔ `Reservation`
  - `Reservation` has `@ManyToOne` to `Vehicle` via `vehicle_id`.
  - `Vehicle` has `@OneToMany(mappedBy = "vehicle")` collection of reservations.

These mappings ensure clear foreign keys, correct ownership, and a proper One‑to‑Many / Many‑to‑One relationship.

#### One‑to‑One (Requirement #6)

- `Reservation` ↔ `Payment`
  - `Payment`:
    - `@OneToOne`
    - `@JoinColumn(name = "reservation_id", unique = true)` (owning side)
  - `Reservation`:
    - `@OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)`
  - Enforces a strict One‑to‑One mapping at the database level using a unique foreign key.

---

### 4. existsBy Methods (Requirement #7)

**UserRepository**
- `boolean existsByUsername(String username);`
- `boolean existsByEmail(String email);`
- `boolean existsByUsernameAndEmail(String username, String email);`
- `boolean existsByEmailAndIdNot(String email, UUID id);`
- `boolean existsByLocationAndRole_Name(Location location, String roleName);`

**ReservationRepository**
- `boolean existsByReservationNumber(String reservationNumber);`

**VehicleFeatureRepository**
- `boolean existsByVehicleAndFeature(Vehicle vehicle, Feature feature);`

Spring Data JPA generates SQL like:
- `SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM users WHERE username = ?`

The controller `GET /api/users/check-exists?username=...` demonstrates how to expose an existence check via REST.

---

### 5. Retrieve Users by Province & Location (Requirement #8)

**Goal**: Retrieve users whose location is in a given province (or any of its children levels), using province **code** or **name**.

Implementation:
- `LocationRepository.findByIdentifier(String identifier)` finds a location by code or name.
- `LocationRepository.findAllDescendantsRaw(UUID parentId)` uses a recursive CTE to get the full subtree of locations starting from a parent.
- `UserRepository.findByLocationIdIn(List<UUID> locationIds)` returns all users whose `location_id` is in the given list.
- `UserService.getUsersByProvince(String identifier)`:
  - Resolves the province by code or name.
  - Validates that the resolved location type is `PROVINCE`.
  - Collects all descendant location IDs (District→Sector→Cell→Village).
  - Fetches all users that belong to any of those locations.
  - Converts them to `UserDTO` including `locationName`, `locationType`, and `provinceName`.
- **Endpoint**: `GET /api/users/by-province?identifier=Kigali` or `identifier=RW-01`.

Additional generic endpoint:
- `GET /api/users/by-location?identifier=...`
  - Returns users attached to the location identified by code or name and **all of its descendants**, regardless of type.
  - Demonstrates that users linked at village level can still be retrieved when querying at sector, district, or province level.

This satisfies:
- Province can be addressed by **code or name**.
- Users are reachable from any level because of the parent‑child `Location` chain.

---

### 6. User Creation Logic (Requirement #9)

**Rule**: Users must be created using **only the Village name or code**. They must not be attached directly to a province, district, sector, or cell.

Implementation:

- **DTO**: `UserCreateRequest` (in `dto` package)
  - Fields: `username`, `email`, `password`, `roleName`, `villageIdentifier`.
  - `villageIdentifier` may be a village code or name.

- **Service**: `UserService.createUser(UserCreateRequest request)`
  - Validates required fields.
  - Uses `LocationRepository.findByIdentifier(request.getVillageIdentifier())` to resolve the location.
  - Ensures that the location type is `VILLAGE`; otherwise throws a validation exception.
  - Loads `Role` by name via `RoleRepository`.
  - Creates a `User` entity:
    - `user.setLocation(villageLocation);` (only village is directly set)
    - Does **not** allow setting province/district/sector/cell directly.
  - Persists the user; hierarchy is inferred later by walking `location.getParent()`.

- **Controller**:
  - `POST /api/users`
  - Body: `UserCreateRequest` JSON
  - Returns `201 Created` with `UserDTO` or appropriate error.

Through the `Location` parent chain (`village → cell → sector → district → province`), the system can compute the full hierarchy for a user without storing redundant foreign keys on the user itself.

---

### 7. Coding Styles & Architecture (Requirement #10)

#### Architectural Styles

- **Layered Architecture**:
  - **Controller layer**: `*Controller` classes under `controller` package.
  - **Service layer**: `*Service` classes under `service` package.
  - **Repository layer**: `*Repository` interfaces under `repository` package.
  - **Model/DTO layer**: entities in `model` and DTOs in `dto`.
- **Separation of Concerns**:
  - Controllers handle HTTP, request/response mapping, and status codes.
  - Services contain business logic and validation.
  - Repositories abstract persistence with Spring Data JPA.
- **RESTful API Design**:
  - Resource-based URIs: `/api/locations`, `/api/users`, `/api/vehicles`, `/api/reservations`.
  - Use of HTTP methods: `GET`, `POST`.
  - JSON request/response bodies using Spring Boot defaults.
- **MVC Pattern**:
  - Model: JPA entities & DTOs.
  - View: JSON over HTTP.
  - Controller: REST controllers mapping requests to services.

#### Naming Conventions

- **Classes/Interfaces**: PascalCase (`UserService`, `LocationRepository`).
- **Methods/Functions**: camelCase (`saveLocation`, `getUsersByProvince`).
- **Variables/Parameters**: camelCase across the codebase.
- **Enums**: UPPER_SNAKE_CASE constants (`ELocationType.PROVINCE`).
- **Packages**: lowercase (`controller`, `service`, `repository`, `model`).

#### Code Organization & Principles

- **Package Structure**: clear separation by layer (`controller`, `service`, `repository`, `model`, `dto`, `model.enums`, `exception`).
- **Single Responsibility Principle**:
  - Each entity models one aggregate.
  - Each service focuses on one domain: locations, users, vehicles, reservations, payments.
- **DRY Principle**:
  - Common pagination patterns are reused and abstracted via `Pageable` and `Page`.
  - Helper methods based on naming conventions in repositories reduce boilerplate queries.
- **Interface vs Implementation**:
  - Repositories are defined as interfaces extending `JpaRepository`.
  - Controllers/services depend on repository interfaces, not implementations.

#### Database / Data Layer

- **ORM Mapping**:
  - JPA annotations on all entities (`@Entity`, `@Table`, `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)`).
- **Repository Pattern**:
  - `UserRepository`, `LocationRepository`, `VehicleRepository`, `ReservationRepository`, `PaymentRepository`, `FeatureRepository`, `VehicleFeatureRepository`, `CustomerRepository`, `RoleRepository`, `PermissionRepository`.
- **UUID Primary Keys**:
  - All main entities use `UUID` as `@Id` with `GenerationType.UUID`.
- **Relationship Mapping**:
  - Self-referential: `Location` parent/children.
  - `@ManyToOne` / `@OneToMany` for `Customer`–`Reservation` and `Vehicle`–`Reservation`.
  - `@OneToOne` for `Reservation`–`Payment`.
  - `@ManyToMany` for `Role`–`Permission` with join table.
- **Field Naming**:
  - Consistent lowerCamelCase for fields; clear, descriptive names.

#### API Design

- **Resource-based endpoints**:
  - `/api/locations`, `/api/users`, `/api/vehicles`, `/api/reservations`, `/api/demo`.
- **HTTP Methods**:
  - `POST` for creation (locations, users, vehicles, reservations, payments).
  - `GET` for retrieval, listing, and searching (with filters).
- **JSON Formats**:
  - All endpoints consume and produce JSON by default using `@RestController`.
- **HTTP Status Codes**:
  - `201 Created` on successful creation (e.g., user, vehicle, reservation).
  - `200 OK` on successful reads.
  - `400 Bad Request` on validation failures.
  - `404 Not Found` when entities do not exist.
- **Query Parameters**:
  - Used for pagination, sorting, and filtering (e.g. `page`, `size`, `sortBy`, `direction`, `identifier`, `status`).

#### Error Handling

- **Centralized Exception Handling**:
  - `GlobalExceptionHandler` (`@RestControllerAdvice`) handles:
    - Validation errors (`IllegalArgumentException`, custom exceptions).
    - Not-found cases.
    - Generic unhandled exceptions.
  - Returns a consistent JSON structure: `timestamp`, `status`, `error`, `message`, `path`.
- **Input Validation**:
  - Service layer validates requirements (e.g., user village type, location hierarchy).
  - Errors are turned into clear messages and appropriate HTTP codes.

#### Dependency Management & Injection

- **Dependency Injection**:
  - Spring manages controllers, services, and repositories.
  - `@Autowired` fields or constructor injection (depending on class) used to inject dependencies.
- **Loose Coupling**:
  - Controllers depend on service interfaces/classes, which in turn depend on repository **interfaces**.
  - Business logic is isolated from persistence concerns.

---

### 8. How to Run the Project

1. **Prerequisites**
   - Java 17+
   - Maven 3+
   - PostgreSQL running and configured in `application.properties`.

2. **Build**

```bash
mvn clean install
```

3. **Run**

```bash
mvn spring-boot:run
```

4. **Sample Endpoints**
- `POST /api/locations/save` – create province/district/sector/cell/village.
- `GET /api/locations` – list locations with pagination & sorting.
- `POST /api/users` – create user using village identifier.
- `GET /api/users/by-province?identifier=Kigali` – users under a given province.
- `GET /api/users/by-location?identifier=...` – users under any location level.
- `GET /api/users` – paginated & sorted users.
- `GET /api/users/check-exists?username=john` – existence check.
- `GET /api/demo/users/paged` – detailed pagination explanation.

This README summarizes how the project satisfies all practical examination requirements and documents the main architectural and coding style decisions.

