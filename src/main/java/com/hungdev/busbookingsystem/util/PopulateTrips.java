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
import java.util.List;

/**
 * Utility to populate trips for existing routes and buses
 */
public class PopulateTrips {

    private static final Logger logger = LoggerFactory.getLogger(PopulateTrips.class);

    public static void main(String[] args) {
        logger.info("Starting to populate trips...");

        try {
            JPAUtil.executeInTransaction(em -> {
                // Get all locations
                List<Location> locations = em.createQuery("SELECT l FROM Location l ORDER BY l.id", Location.class)
                        .getResultList();

                logger.info("Found {} locations", locations.size());
                for (Location loc : locations) {
                    logger.info("Location {}: {} ({})", loc.getId(), loc.getName(), loc.getCity());
                }

                // Get or create routes
                List<Route> routes = em.createQuery("SELECT r FROM Route r", Route.class).getResultList();
                logger.info("Found {} existing routes", routes.size());

                if (routes.isEmpty()) {
                    logger.info("Creating routes...");
                    // Create routes between major cities
                    // Assuming locations based on the log output
                    if (locations.size() >= 4) {
                        // Route 1: Hà Nội (id 1 or 2) -> TP.HCM (id 3 or 4)
                        routes.add(createRoute(em, locations.get(0), locations.get(2), 1720.0));
                        routes.add(createRoute(em, locations.get(0), locations.get(3), 1720.0));
                        routes.add(createRoute(em, locations.get(1), locations.get(2), 1720.0));
                        routes.add(createRoute(em, locations.get(1), locations.get(3), 1720.0));

                        // If there are Hải Phòng locations
                        if (locations.size() >= 6) {
                            routes.add(createRoute(em, locations.get(0), locations.get(4), 120.0));
                            routes.add(createRoute(em, locations.get(1), locations.get(5), 120.0));
                        }
                    }
                }

                // Get or create buses
                List<Bus> buses = em.createQuery("SELECT b FROM Bus b", Bus.class).getResultList();
                logger.info("Found {} existing buses", buses.size());

                if (buses.isEmpty()) {
                    logger.info("Creating buses...");
                    buses.add(createBus(em, "29A-12345", "Hyundai Universe", 45, 2020));
                    buses.add(createBus(em, "30B-67890", "Mercedes-Benz Travego", 50, 2021));
                    buses.add(createBus(em, "51C-11111", "Thaco Mobihome", 40, 2019));
                    buses.add(createBus(em, "92D-22222", "Samco Felix", 35, 2022));
                    buses.add(createBus(em, "43E-33333", "Hyundai County", 29, 2018));
                }

                // Check if trips already exist
                Long tripCount = em.createQuery("SELECT COUNT(t) FROM Trip t", Long.class).getSingleResult();
                logger.info("Found {} existing trips", tripCount);

                if (tripCount == 0) {
                    logger.info("Creating trips...");
                    LocalDate today = LocalDate.now();

                    // Create trips for each route
                    for (Route route : routes) {
                        for (int day = 0; day < 7; day++) {
                            for (Bus bus : buses) {
                                // Morning trip
                                createTrip(em, route, bus, today.plusDays(day),
                                          LocalTime.of(6, 0), LocalTime.of(14, 0),
                                          new BigDecimal("250.00"));

                                // Evening trip
                                createTrip(em, route, bus, today.plusDays(day),
                                          LocalTime.of(18, 0), LocalTime.of(2, 0),
                                          new BigDecimal("280.00"));
                            }
                        }
                    }
                }

                tripCount = em.createQuery("SELECT COUNT(t) FROM Trip t", Long.class).getSingleResult();
                logger.info("Total trips in database: {}", tripCount);
            });

            logger.info("Trip population completed successfully!");

        } catch (Exception e) {
            logger.error("Failed to populate trips", e);
        } finally {
            JPAUtil.shutdown();
        }
    }

    private static Route createRoute(EntityManager em, Location start, Location end, Double distance) {
        // Check if route already exists
        List<Route> existing = em.createQuery(
                "SELECT r FROM Route r WHERE r.startLocation.id = :startId AND r.endLocation.id = :endId",
                Route.class)
                .setParameter("startId", start.getId())
                .setParameter("endId", end.getId())
                .getResultList();

        if (!existing.isEmpty()) {
            logger.info("Route already exists: {} -> {}", start.getName(), end.getName());
            return existing.get(0);
        }

        Route route = new Route(start, end, distance);
        em.persist(route);
        logger.info("Created route: {} -> {} ({} km)", start.getName(), end.getName(), distance);
        return route;
    }

    private static Bus createBus(EntityManager em, String licensePlate, String model, int totalSeats, int manufactureYear) {
        Bus bus = new Bus(licensePlate, model, totalSeats, manufactureYear);
        em.persist(bus);
        logger.info("Created bus: {} {} ({} seats)", licensePlate, model, totalSeats);
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
