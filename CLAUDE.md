# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Bus Reservation System - A Java-based bus booking management system using JPA/Hibernate for persistence. This is currently a console application demonstrating the domain model and business logic.

**Technology Stack:**
- Java 17
- Maven (build tool)
- Java Swing (GUI framework)
- Jakarta Persistence API (JPA) 3.1.0
- Hibernate ORM 6.4.4
- PostgreSQL 42.7.2 (database driver)
- HikariCP 5.1.0 (connection pooling)
- BCrypt 0.4 (password hashing)
- SLF4J + Logback (logging)

## Build & Run Commands

```bash
# Compile the project
mvn compile

# Run the GUI application (Login/Register)
mvn exec:java -Dexec.mainClass="com.hungdev.busbookingsystem.BusBookingApp"

# Test database connection (CLI)
mvn exec:java -Dexec.mainClass="com.hungdev.busbookingsystem.DatabaseConnectionTest"

# Run old demo (no database - in-memory only)
mvn exec:java -Dexec.mainClass="com.hungdev.busbookingsystem.BusBookingSystem"

# Clean and rebuild
mvn clean compile

# Package as JAR
mvn package

# Run packaged JAR
java -jar target/bus-booking-system-1.0.0.jar
```

## Database Setup

### Prerequisites
1. Install PostgreSQL (version 12+)
2. Create database and run schema script

### Initial Setup

```bash
# 1. Create database
createdb bus_booking_db

# 2. Run the SQL schema script
psql -d bus_booking_db -f script.sql

# 3. Verify database connection
mvn exec:java -Dexec.mainClass="com.hungdev.busbookingsystem.DatabaseConnectionTest"
```

### Configuration Files

**Database Configuration:** `src/main/resources/database.properties`
```properties
db.url=jdbc:postgresql://localhost:5432/bus_booking_db
db.username=postgres
db.password=postgres
```

**JPA Configuration:** `src/main/resources/META-INF/persistence.xml`
- Persistence unit name: `bus-booking-pu`
- Default schema validation: `validate` (does not modify database)
- Change to `update` for development if you want Hibernate to auto-update schema

### Using the Database

**JPAUtil Helper Class** at `src/main/java/com/hungdev/busbookingsystem/util/JPAUtil.java`

Example usage:
```java
// Read operation
List<User> users = JPAUtil.executeInReadOnly(em ->
    em.createQuery("SELECT u FROM User u", User.class).getResultList()
);

// Write operation
JPAUtil.executeInTransaction(em -> {
    User user = new User("username", "email@example.com", "John", "Doe", "password", "CUSTOMER");
    em.persist(user);
});
```

## Architecture

### Domain Model Structure

The application follows a **domain-driven design** with JPA entities representing the core business domain:

```
User (customer/admin)
  └── Booking (reservation)
      ├── Ticket (individual seats)
      └── Payment (transaction records)

Trip (scheduled bus journey)
  ├── Route (start/end locations + distance)
  ├── Bus (vehicle with capacity)
  └── Booking (multiple bookings per trip)
```

### Key Entity Relationships

1. **User → Booking**: One-to-Many
   - A user can have multiple bookings
   - User entity manages authentication and role-based access

2. **Trip → Booking**: One-to-Many
   - A trip can have multiple bookings
   - Trip calculates available seats by checking non-cancelled bookings

3. **Booking → Ticket**: One-to-Many with orphan removal
   - Each booking contains multiple tickets (seats)
   - Tickets are automatically removed when booking is deleted

4. **Route → Trip**: One-to-Many
   - A route defines the path between two locations
   - Multiple trips can use the same route

5. **Bus → Trip**: One-to-Many
   - A bus can be scheduled for multiple trips
   - Bus defines total seat capacity

### Business Logic Location

- **Available seats calculation**: `Trip.getAvailableSeats()` at src/main/java/com/hungdev/busbookingsystem/model/Trip.java:106
- **Ticket management**: `Booking.addTicket()` and `Booking.removeTicket()` at src/main/java/com/hungdev/busbookingsystem/model/Booking.java:119-127
- **Booking status**: Managed as string enum: "Pending", "Confirmed", "Cancelled"
- **Payment status**: Managed as string enum: "Succeeded", "Failed", "Pending"

### Current State

The application currently has:
- **GUI Application**: Java Swing-based login/register system with authentication
  - Run with: `mvn exec:java -Dexec.mainClass="com.hungdev.busbookingsystem.BusBookingApp"`
  - Features: User registration, login, logout, dashboard
  - Password security: BCrypt hashing (12 rounds)
- **Database**: Fully configured PostgreSQL connection via JPA/Hibernate
- **Entities**: All models matching the database schema from `script.sql`
- **Services**: UserService for authentication and user management
- **Utilities**: JPAUtil helper class for database operations
- **Testing**: DatabaseConnectionTest to verify database connectivity
- Old `BusBookingSystem.java` demo (in-memory only, no database)
- No repository layer or REST API yet
- No test suite

### Package Structure

```
com.hungdev.busbookingsystem
├── BusBookingApp.java (main GUI application entry point)
├── BusBookingSystem.java (old demo - no database)
├── DatabaseConnectionTest.java (database connection test)
├── gui/
│   ├── LoginFrame.java (login window)
│   ├── RegisterFrame.java (sign up window)
│   └── MainFrame.java (dashboard after login)
├── model/
│   ├── Booking.java
│   ├── Bus.java
│   ├── Location.java
│   ├── Payment.java
│   ├── Route.java
│   ├── Seat.java
│   ├── Ticket.java
│   ├── Trip.java
│   ├── User.java
│   └── UserSession.java
├── service/
│   └── UserService.java (authentication & user management)
└── util/
    └── JPAUtil.java (EntityManager factory utility)
```

## Development Guidelines

### When Adding New Features

1. **Service Layer**: Create `service/` package for business logic
   - Use `JPAUtil.executeInTransaction()` for write operations
   - Use `JPAUtil.executeInReadOnly()` for read operations
   - Example: `UserService.java`, `BookingService.java`

2. **Repository Layer**: Create `repository/` package for data access
   - Encapsulate JPA queries and CRUD operations
   - Example: `UserRepository.java`, `TripRepository.java`

3. **API Layer**: If adding REST endpoints, create `controller/` or `api/` package
   - Consider using Spring Boot or JAX-RS for REST API

4. **Database Connection**: Already configured in `src/main/resources/META-INF/persistence.xml`
   - Modify connection details in `persistence.xml` or use environment variables

### When Modifying Entities

1. Update JPA annotations if changing table structure
2. Update bidirectional relationships on both sides (e.g., if modifying Booking, check Trip)
3. Consider cascade operations impact on related entities
4. Update `toString()` methods if adding fields that should be displayed

### Data Type Conventions

- **IDs**: `Long` for User, `Integer` for other entities
- **Money**: `BigDecimal` with precision=12, scale=2
- **Timestamps**: `LocalDateTime`
- **Status fields**: String with predefined values (consider enum refactoring)

### Refactoring Opportunities

- Status fields (Booking, Payment) should be Java enums instead of strings
- User roles should be extracted to enum
- Consider adding validation annotations (Jakarta Bean Validation)
- Extract business logic from entities to service classes
- Add proper persistence configuration and EntityManager usage
