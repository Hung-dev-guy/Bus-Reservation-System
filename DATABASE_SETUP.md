# Database Setup Guide

## Problem: "relation public.trips does not exist"

This error occurs when the database tables haven't been created yet. Here are the solutions:

## Solutions

### Solution 1: Use Hibernate Auto Schema Generation (Current Setup)

The `persistence.xml` is now configured with `hibernate.hbm2ddl.auto=update`. This means:
- Tables will be **automatically created** when the application starts
- Tables will **persist** even after the application stops
- Schema will be **updated** automatically when entity classes change

**Steps:**
1. Run your application once (`BusBookingApp.java`)
2. Hibernate will automatically create all tables
3. Tables will remain in the database for IntelliJ queries

### Solution 2: Manually Create Tables Using SQL Script

If you prefer manual control or need to create tables before running the app:

```bash
# Connect to PostgreSQL
psql -U postgres -d bus_booking_management

# Execute the schema creation script
\i schema.sql

# Or in one command:
psql -U postgres -d bus_booking_management -f schema.sql
```

### Solution 3: Using IntelliJ Database Console

1. Open IntelliJ's Database tool window
2. Connect to your database: `jdbc:postgresql://localhost:5432/bus_booking_management`
3. Open and execute the `schema.sql` file

## Configuration Details

### Database Connection
- **Host:** localhost
- **Port:** 5432
- **Database:** bus_booking_management
- **Username:** postgres
- **Password:** hieudevdut277

### Hibernate Settings Comparison

| Setting | Behavior | Use Case |
|---------|----------|----------|
| `create-drop` | Creates tables on startup, drops on shutdown | Testing only |
| `create` | Creates tables on startup, keeps them | Initial development |
| `update` | Updates schema automatically | **Development (Current)** |
| `validate` | Only validates schema | Production |
| `none` | No schema management | Manual control |

## Verifying Tables Were Created

Run this query in IntelliJ's database console:

```sql
-- List all tables
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public'
ORDER BY table_name;

-- Expected tables:
-- bookings, buses, locations, payments, routes, 
-- seats, tickets, trips, user_sessions, users
```

## Populating Test Data

After tables are created, you can populate test data:

1. Run the application
2. The `TestDataInitializer` or `PopulateTrips` utility classes should handle initial data

Or manually:

```sql
-- Example: Insert a test location
INSERT INTO locations (name, city, address) 
VALUES ('Central Station', 'Hanoi', '123 Main St');

-- Example: Insert a test bus
INSERT INTO buses (bus_number, bus_type, total_seats, amenities) 
VALUES ('BUS001', 'LUXURY', 40, 'WiFi, AC, TV');
```

## Troubleshooting

### Error: "database does not exist"
```bash
# Create the database
createdb -U postgres bus_booking_management

# Or using SQL
psql -U postgres
CREATE DATABASE bus_booking_management;
```

### Error: "password authentication failed"
- Check your PostgreSQL password
- Update `persistence.xml` with correct credentials

### Tables still don't exist after running app
1. Check application logs for errors
2. Ensure `JPAUtil.getEntityManagerFactory()` is called
3. Verify database connection is successful

## Production Deployment

For production, change `hibernate.hbm2ddl.auto` to `validate` and use proper migration tools:
- Flyway
- Liquibase

This prevents automatic schema changes in production.
