# Bus Booking System - Setup Guide

This guide will help you set up and run the Bus Booking System application.

## Prerequisites

Before running the project, ensure you have the following installed:

1. **Java Development Kit (JDK) 17 or higher**
   ```bash
   java -version
   # Should show version 17 or higher
   ```

2. **Maven 3.6 or higher**
   ```bash
   mvn -version
   # Should show Maven version
   ```

3. **PostgreSQL 12 or higher**
   ```bash
   psql --version
   # Should show PostgreSQL version
   ```

## Step-by-Step Setup

### 1. Database Setup

#### a. Create Database
```bash
# Connect to PostgreSQL (enter password when prompted)
psql -U postgres

# In psql console, create database
CREATE DATABASE bus_booking_management;

# Exit psql
\q
```

#### b. Run Schema Script
```bash
# From project root directory
psql -U postgres -d bus_booking_management -f script.sql

# Enter password: hieudevdut277 (when prompted)
```

#### c. Verify Database
```bash
# Connect to database
psql -U postgres -d bus_booking_management

# List tables
\dt

# You should see tables: users, buses, locations, routes, trips, bookings, tickets, payments, seats, user_sessions, etc.

# Exit
\q
```

### 2. Configure Database Connection

The database configuration is already set in `src/main/resources/META-INF/persistence.xml`:
```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/bus_booking_management"/>
<property name="jakarta.persistence.jdbc.user" value="postgres"/>
<property name="jakarta.persistence.jdbc.password" value="hieudevdut277"/>
```

If your PostgreSQL credentials are different, update these values.

### 3. Build the Project

```bash
# Clean and compile
mvn clean compile

# This will download all dependencies (first time may take a few minutes)
```

### 4. Run the Application

#### Option 1: Run GUI Application (Recommended)
```bash
mvn exec:java -Dexec.mainClass="com.hungdev.busbookingsystem.BusBookingApp"
```

This will open the login window.

#### Option 2: Test Database Connection First
```bash
mvn exec:java -Dexec.mainClass="com.hungdev.busbookingsystem.DatabaseConnectionTest"
```

This will verify your database connection is working.

## Using the Application

### First Time Use

1. **Register a New Account**
   - Click "Sign Up" button on login screen
   - Fill in the registration form:
     - First Name: Your first name
     - Last Name: Your last name
     - Username: Choose a unique username (min 3 characters)
     - Email: Your email address
     - Password: Choose a strong password (min 8 characters)
     - Confirm Password: Re-enter password
   - Click "Sign Up"
   - You'll see a success message

2. **Login**
   - Return to login screen (automatically after signup)
   - Enter your username (or email) and password
   - Click "Login"
   - You'll be redirected to the dashboard

3. **Dashboard**
   - View welcome message with your name
   - See your user role and email
   - Click "Logout" to sign out

## Troubleshooting

### Issue: "Failed to connect to database"

**Solution 1: Check PostgreSQL is running**
```bash
# On macOS/Linux
sudo systemctl status postgresql

# Or
ps aux | grep postgres
```

**Solution 2: Verify database exists**
```bash
psql -U postgres -l | grep bus_booking_management
```

**Solution 3: Test connection manually**
```bash
psql -U postgres -d bus_booking_management
# If this fails, check your password and database name
```

### Issue: "Maven command not found"

**Solution: Install Maven**
```bash
# On macOS
brew install maven

# On Ubuntu/Debian
sudo apt-get install maven

# Verify installation
mvn -version
```

### Issue: "Java version error"

**Solution: Install JDK 17**
```bash
# On macOS
brew install openjdk@17

# On Ubuntu/Debian
sudo apt-get install openjdk-17-jdk

# Set JAVA_HOME
export JAVA_HOME=/path/to/jdk-17
```

### Issue: "Username already exists" during registration

**Solution:** Choose a different username or email address. Each must be unique in the system.

### Issue: Port 5432 already in use

**Solution:** PostgreSQL is probably already running. Check with:
```bash
sudo lsof -i :5432
```

## Package as Standalone JAR (Optional)

To create a standalone executable JAR:

```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/bus-booking-system-1.0.0.jar
```

## Development Mode

If you want to run with automatic schema updates (for development):

1. Edit `src/main/resources/META-INF/persistence.xml`
2. Change this line:
   ```xml
   <property name="hibernate.hbm2ddl.auto" value="validate"/>
   ```
   To:
   ```xml
   <property name="hibernate.hbm2ddl.auto" value="update"/>
   ```

⚠️ **Warning:** Only use `update` in development. Always use `validate` in production.

## Quick Reference

| Command | Purpose |
|---------|---------|
| `mvn compile` | Compile the project |
| `mvn clean compile` | Clean and rebuild |
| `mvn exec:java -Dexec.mainClass="com.hungdev.busbookingsystem.BusBookingApp"` | Run GUI application |
| `mvn exec:java -Dexec.mainClass="com.hungdev.busbookingsystem.DatabaseConnectionTest"` | Test database connection |
| `mvn package` | Build JAR file |
| `psql -U postgres -d bus_booking_management` | Connect to database |

## Getting Help

If you encounter issues:

1. Check the logs in the console output
2. Verify all prerequisites are installed
3. Ensure PostgreSQL is running
4. Check database credentials in `persistence.xml`
5. Review the error messages carefully

## Next Steps

After successfully running the application:
- Create your account
- Explore the dashboard
- Future features will include trip booking, ticket management, and payment processing

---

**Note:** Make sure PostgreSQL is running before starting the application!
