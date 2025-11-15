package com.hungdev.busbookingsystem.service;

import com.hungdev.busbookingsystem.model.Trip;
import com.hungdev.busbookingsystem.model.Location;
import com.hungdev.busbookingsystem.util.JPAUtil;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Service class for trip management and search
 */
public class TripService {

    private static final Logger logger = LoggerFactory.getLogger(TripService.class);
    private static TripService instance;

    private TripService() {}

    public static TripService getInstance() {
        if (instance == null) {
            synchronized (TripService.class) {
                if (instance == null) {
                    instance = new TripService();
                }
            }
        }
        return instance;
    }

    /**
     * Search trips by origin, destination and date
     */
    public List<Trip> searchTrips(Long originId, Long destinationId, LocalDate date) {
        return JPAUtil.executeInReadOnly(em -> {
            OffsetDateTime startOfDay = date.atStartOfDay().atOffset(java.time.ZoneOffset.UTC);
            OffsetDateTime endOfDay = date.plusDays(1).atStartOfDay().atOffset(java.time.ZoneOffset.UTC);

            String jpql = "SELECT t FROM Trip t " +
                    "JOIN FETCH t.route r " +
                    "JOIN FETCH t.bus b " +
                    "WHERE r.startLocation.id = :originId " +
                    "AND r.endLocation.id = :destinationId " +
                    "AND t.departureTime >= :startOfDay " +
                    "AND t.departureTime < :endOfDay " +
                    "ORDER BY t.departureTime ASC";

            TypedQuery<Trip> query = em.createQuery(jpql, Trip.class);
            query.setParameter("originId", originId);
            query.setParameter("destinationId", destinationId);
            query.setParameter("startOfDay", startOfDay);
            query.setParameter("endOfDay", endOfDay);

            List<Trip> trips = query.getResultList();
            logger.info("Found {} trips from {} to {} on {}", trips.size(), originId, destinationId, date);
            return trips;
        });
    }

    /**
     * Get all available locations
     */
    public List<Location> getAllLocations() {
        return JPAUtil.executeInReadOnly(em -> {
            TypedQuery<Location> query = em.createQuery(
                    "SELECT l FROM Location l ORDER BY l.name ASC",
                    Location.class);
            return query.getResultList();
        });
    }

    /**
     * Get trip by ID with all related data
     */
    public Trip getTripById(Long tripId) {
        return JPAUtil.executeInReadOnly(em -> {
            String jpql = "SELECT t FROM Trip t " +
                    "JOIN FETCH t.route r " +
                    "JOIN FETCH r.startLocation " +
                    "JOIN FETCH r.endLocation " +
                    "JOIN FETCH t.bus " +
                    "LEFT JOIN FETCH t.bookings " +
                    "WHERE t.id = :tripId";

            TypedQuery<Trip> query = em.createQuery(jpql, Trip.class);
            query.setParameter("tripId", tripId);

            return query.getSingleResult();
        });
    }

    /**
     * Get all upcoming trips
     */
    public List<Trip> getUpcomingTrips(int limit) {
        return JPAUtil.executeInReadOnly(em -> {
            OffsetDateTime now = OffsetDateTime.now();

            String jpql = "SELECT t FROM Trip t " +
                    "JOIN FETCH t.route r " +
                    "JOIN FETCH t.bus b " +
                    "WHERE t.departureTime > :now " +
                    "ORDER BY t.departureTime ASC";

            TypedQuery<Trip> query = em.createQuery(jpql, Trip.class);
            query.setParameter("now", now);
            query.setMaxResults(limit);

            return query.getResultList();
        });
    }
}
