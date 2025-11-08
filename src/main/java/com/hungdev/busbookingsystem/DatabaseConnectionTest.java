package com.hungdev.busbookingsystem;

import com.hungdev.busbookingsystem.model.*;
import com.hungdev.busbookingsystem.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Test class to verify PostgreSQL database connection and basic CRUD operations.
 * Run this after setting up your database with the script.sql schema.
 */
public class DatabaseConnectionTest {

    public static void main(String[] args) {
        System.out.println("=== PostgreSQL Database Connection Test ===\n");

        try {
            // Test 1: Connection Test
            testConnection();

            // Test 2: Query Existing Data
            testQueryLocations();

            // Test 3: Insert Example (uncomment to test)
            // testInsertLocation();

            System.out.println("\n=== All Tests Completed Successfully ===");

        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Shutdown EntityManagerFactory
            JPAUtil.shutdown();
        }
    }

    /**
     * Test 1: Verify database connection
     */
    private static void testConnection() {
        System.out.println("TEST 1: Database Connection");
        System.out.println("----------------------------");

        JPAUtil.executeInReadOnly(em -> {
            // Simple query to verify connection
            Long count = em.createQuery("SELECT COUNT(u) FROM User u", Long.class)
                    .getSingleResult();
            System.out.println("Connected to database successfully!");
            System.out.println("Total users in database: " + count);
            return null;
        });

        System.out.println();
    }

    /**
     * Test 2: Query existing locations
     */
    private static void testQueryLocations() {
        System.out.println("TEST 2: Query Locations");
        System.out.println("------------------------");

        List<Location> locations = JPAUtil.executeInReadOnly(em ->
                em.createQuery("SELECT l FROM Location l", Location.class)
                        .getResultList()
        );

        if (locations.isEmpty()) {
            System.out.println("No locations found in database.");
        } else {
            System.out.println("Found " + locations.size() + " location(s):");
            for (Location location : locations) {
                System.out.println("  - " + location.getName() + " (" + location.getCity() + ")");
            }
        }

        System.out.println();
    }

    /**
     * Test 3: Insert a new location (example)
     * Uncomment in main() to test insert operations
     */
    private static void testInsertLocation() {
        System.out.println("TEST 3: Insert New Location");
        System.out.println("----------------------------");

        JPAUtil.executeInTransaction(em -> {
            Location location = new Location("Test Station", "Test City");
            em.persist(location);
            System.out.println("Inserted new location: " + location.getName());
        });

        System.out.println();
    }

    /**
     * Example: Complex query with relationships
     */
    @SuppressWarnings("unused")
    private static void testQueryTripsWithRoutes() {
        System.out.println("EXAMPLE: Query Trips with Routes");
        System.out.println("----------------------------------");

        List<Trip> trips = JPAUtil.executeInReadOnly(em ->
                em.createQuery(
                        "SELECT t FROM Trip t " +
                        "JOIN FETCH t.route r " +
                        "JOIN FETCH r.startLocation " +
                        "JOIN FETCH r.endLocation " +
                        "WHERE t.departureTime > :now",
                        Trip.class)
                .setParameter("now", OffsetDateTime.now())
                .getResultList()
        );

        System.out.println("Found " + trips.size() + " upcoming trip(s):");
        for (Trip trip : trips) {
            System.out.println("  - " + trip.getRoute().getStartLocation().getCity() +
                    " to " + trip.getRoute().getEndLocation().getCity() +
                    " at " + trip.getDepartureTime());
        }

        System.out.println();
    }

    /**
     * Example: Create a complete booking flow
     */
    @SuppressWarnings("unused")
    private static void testCreateBooking() {
        JPAUtil.executeInTransaction(em -> {
            // Get existing user and trip
            User user = em.find(User.class, 1L);
            Trip trip = em.find(Trip.class, 1L);

            if (user == null || trip == null) {
                System.out.println("User or Trip not found");
                return;
            }

            // Create booking
            Booking booking = new Booking(user, trip, 2, new BigDecimal("1000000"));
            em.persist(booking);

            // Create tickets
            Seat seat1 = em.find(Seat.class, 1L);
            Seat seat2 = em.find(Seat.class, 2L);

            Ticket ticket1 = new Ticket(booking, trip, seat1, "John Doe", new BigDecimal("500000"));
            Ticket ticket2 = new Ticket(booking, trip, seat2, "Jane Doe", new BigDecimal("500000"));

            em.persist(ticket1);
            em.persist(ticket2);

            // Create payment
            Payment payment = new Payment(booking, new BigDecimal("1000000"), "Credit Card");
            payment.setTransactionCode("TXN-" + System.currentTimeMillis());
            payment.setStatus("Succeeded");
            em.persist(payment);

            System.out.println("Booking created successfully with ID: " + booking.getId());
        });
    }
}
