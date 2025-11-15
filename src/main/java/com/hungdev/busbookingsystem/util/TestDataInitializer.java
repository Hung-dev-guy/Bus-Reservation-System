package com.hungdev.busbookingsystem.util;

import com.hungdev.busbookingsystem.model.*;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Initialize test data for the bus booking system
 */
public class TestDataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(TestDataInitializer.class);

    public static void initializeTestData() {
        logger.info("Starting test data initialization...");

        try {
            JPAUtil.executeInTransaction(em -> {
                // Check if data already exists
                Long tripCount = em.createQuery("SELECT COUNT(t) FROM Trip t", Long.class)
                        .getSingleResult();

                if (tripCount > 0) {
                    logger.info("Test data already exists. Skipping initialization.");
                    return;
                }

                // Clear existing data (in reverse order of dependency)
                logger.info("No trips found. Clearing and initializing test data...");
                em.createQuery("DELETE FROM Ticket").executeUpdate();
                em.createQuery("DELETE FROM Booking").executeUpdate();
                em.createQuery("DELETE FROM Trip").executeUpdate();
                em.createQuery("DELETE FROM Seat").executeUpdate();
                em.createQuery("DELETE FROM Route").executeUpdate();
                em.createQuery("DELETE FROM Bus").executeUpdate();
                em.createQuery("DELETE FROM Location").executeUpdate();

                // Create Locations
                Location hanoi = createLocation(em, "Hanoi", "Hanoi");
                Location hochiminh = createLocation(em, "Ho Chi Minh City", "Ho Chi Minh");
                Location danang = createLocation(em, "Da Nang", "Da Nang");
                Location haiphong = createLocation(em, "Hai Phong", "Hai Phong");
                Location nhatrang = createLocation(em, "Nha Trang", "Khanh Hoa");

                // Create Routes
                Route route1 = createRoute(em, hanoi, hochiminh, 1720.0);
                Route route2 = createRoute(em, hanoi, danang, 763.0);
                Route route3 = createRoute(em, hochiminh, danang, 964.0);
                Route route4 = createRoute(em, hanoi, haiphong, 120.0);
                Route route5 = createRoute(em, hochiminh, nhatrang, 430.0);

                // Create Buses
                Bus bus1 = createBus(em, "29A-12345", "Hyundai Universe", 45, 2020);
                Bus bus2 = createBus(em, "30B-67890", "Mercedes-Benz Travego", 50, 2021);
                Bus bus3 = createBus(em, "51C-11111", "Thaco Mobihome", 40, 2019);
                Bus bus4 = createBus(em, "92D-22222", "Samco Felix", 35, 2022);
                Bus bus5 = createBus(em, "43E-33333", "Hyundai County", 29, 2018);

                // Create Trips (next 7 days)
                LocalDate today = LocalDate.now();

                // Hanoi to Ho Chi Minh
                createTrip(em, route1, bus1, today.plusDays(1), LocalTime.of(6, 0), LocalTime.of(20, 0), new BigDecimal("450.00"));
                createTrip(em, route1, bus2, today.plusDays(1), LocalTime.of(8, 30), LocalTime.of(22, 30), new BigDecimal("500.00"));
                createTrip(em, route1, bus1, today.plusDays(2), LocalTime.of(7, 0), LocalTime.of(21, 0), new BigDecimal("450.00"));
                createTrip(em, route1, bus2, today.plusDays(3), LocalTime.of(6, 0), LocalTime.of(20, 0), new BigDecimal("480.00"));

                // Hanoi to Da Nang
                createTrip(em, route2, bus3, today, LocalTime.of(22, 0), LocalTime.of(8, 0), new BigDecimal("280.00"));
                createTrip(em, route2, bus4, today.plusDays(1), LocalTime.of(21, 30), LocalTime.of(7, 30), new BigDecimal("300.00"));
                createTrip(em, route2, bus3, today.plusDays(2), LocalTime.of(22, 0), LocalTime.of(8, 0), new BigDecimal("280.00"));
                createTrip(em, route2, bus4, today.plusDays(3), LocalTime.of(21, 0), LocalTime.of(7, 0), new BigDecimal("290.00"));

                // Ho Chi Minh to Da Nang
                createTrip(em, route3, bus2, today.plusDays(1), LocalTime.of(8, 0), LocalTime.of(19, 0), new BigDecimal("350.00"));
                createTrip(em, route3, bus3, today.plusDays(2), LocalTime.of(9, 0), LocalTime.of(20, 0), new BigDecimal("340.00"));
                createTrip(em, route3, bus2, today.plusDays(3), LocalTime.of(7, 30), LocalTime.of(18, 30), new BigDecimal("360.00"));

                // Hanoi to Hai Phong (short trips)
                createTrip(em, route4, bus5, today, LocalTime.of(6, 0), LocalTime.of(8, 30), new BigDecimal("80.00"));
                createTrip(em, route4, bus5, today, LocalTime.of(10, 0), LocalTime.of(12, 30), new BigDecimal("80.00"));
                createTrip(em, route4, bus5, today, LocalTime.of(14, 0), LocalTime.of(16, 30), new BigDecimal("80.00"));
                createTrip(em, route4, bus5, today.plusDays(1), LocalTime.of(6, 0), LocalTime.of(8, 30), new BigDecimal("80.00"));
                createTrip(em, route4, bus5, today.plusDays(1), LocalTime.of(14, 0), LocalTime.of(16, 30), new BigDecimal("80.00"));

                // Ho Chi Minh to Nha Trang
                createTrip(em, route5, bus4, today.plusDays(1), LocalTime.of(6, 0), LocalTime.of(12, 0), new BigDecimal("180.00"));
                createTrip(em, route5, bus4, today.plusDays(2), LocalTime.of(7, 0), LocalTime.of(13, 0), new BigDecimal("180.00"));
                createTrip(em, route5, bus3, today.plusDays(3), LocalTime.of(6, 30), LocalTime.of(12, 30), new BigDecimal("190.00"));

                logger.info("Test data initialization completed successfully!");
                logger.info("Created: 5 locations, 5 routes, 5 buses, and 20 trips");
            });

        } catch (Exception e) {
            logger.error("Failed to initialize test data", e);
        }
    }

    private static Location createLocation(EntityManager em, String name, String city) {
        Location location = new Location(name, city);
        em.persist(location);
        return location;
    }

    private static Route createRoute(EntityManager em, Location start, Location end, Double distance) {
        Route route = new Route(start, end, distance);
        em.persist(route);
        return route;
    }

    private static Bus createBus(EntityManager em, String licensePlate, String model, int totalSeats, int manufactureYear) {
        Bus bus = new Bus(licensePlate, model, totalSeats, manufactureYear);
        em.persist(bus);
        return bus;
    }

    private static Trip createTrip(EntityManager em, Route route, Bus bus, LocalDate date,
                                    LocalTime departureTime, LocalTime arrivalTime, BigDecimal price) {
        OffsetDateTime departure = OffsetDateTime.of(date, departureTime, ZoneOffset.UTC);

        // Handle overnight trips
        OffsetDateTime arrival;
        if (arrivalTime.isBefore(departureTime)) {
            arrival = OffsetDateTime.of(date.plusDays(1), arrivalTime, ZoneOffset.UTC);
        } else {
            arrival = OffsetDateTime.of(date, arrivalTime, ZoneOffset.UTC);
        }

        Trip trip = new Trip(route, bus, departure, arrival, price);
        em.persist(trip);
        return trip;
    }
}
