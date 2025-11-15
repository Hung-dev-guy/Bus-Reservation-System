package com.hungdev.busbookingsystem.service;

import com.hungdev.busbookingsystem.model.*;
import com.hungdev.busbookingsystem.util.JPAUtil;
import com.hungdev.busbookingsystem.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Service class for admin operations
 */
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private static AdminService instance;

    private AdminService() {}

    public static AdminService getInstance() {
        if (instance == null) {
            synchronized (AdminService.class) {
                if (instance == null) {
                    instance = new AdminService();
                }
            }
        }
        return instance;
    }

    // ============= USER MANAGEMENT =============

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return JPAUtil.executeInReadOnly(em -> {
            String jpql = "SELECT u FROM User u ORDER BY u.dateJoined DESC";
            return em.createQuery(jpql, User.class).getResultList();
        });
    }

    /**
     * Get user by ID
     */
    public User getUserById(Long userId) {
        return JPAUtil.executeInReadOnly(em -> em.find(User.class, userId));
    }

    /**
     * Create new user
     */
    public boolean createUser(String username, String email, String firstName, String lastName, 
                             String password, String role) {
        try {
            JPAUtil.executeInTransaction(em -> {
                // Check if username or email already exists
                Long count = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.username = :username OR u.email = :email", 
                    Long.class)
                    .setParameter("username", username)
                    .setParameter("email", email)
                    .getSingleResult();

                if (count > 0) {
                    throw new RuntimeException("Username or email already exists");
                }

                User user = new User(username, email, firstName, lastName, 
                                    PasswordUtil.hashPassword(password), role);
                user.setPermissions("{}");
                em.persist(user);
                logger.info("User created: {}", username);
            });
            return true;
        } catch (Exception e) {
            logger.error("Failed to create user", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Update user
     */
    public boolean updateUser(Long userId, String email, String firstName, String lastName, 
                             String role, Boolean isActive) {
        try {
            JPAUtil.executeInTransaction(em -> {
                User user = em.find(User.class, userId);
                if (user == null) {
                    throw new RuntimeException("User not found");
                }

                user.setEmail(email);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setRole(role);
                user.setIsActive(isActive);
                
                em.merge(user);
                logger.info("User updated: {}", user.getUsername());
            });
            return true;
        } catch (Exception e) {
            logger.error("Failed to update user", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Delete user
     */
    public boolean deleteUser(Long userId) {
        try {
            JPAUtil.executeInTransaction(em -> {
                User user = em.find(User.class, userId);
                if (user == null) {
                    throw new RuntimeException("User not found");
                }
                
                // Check if user has bookings
                Long bookingCount = em.createQuery(
                    "SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId", Long.class)
                    .setParameter("userId", userId)
                    .getSingleResult();

                if (bookingCount > 0) {
                    throw new RuntimeException("Cannot delete user with existing bookings. Deactivate instead.");
                }

                em.remove(user);
                logger.info("User deleted: {}", user.getUsername());
            });
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete user", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    // ============= TRIP MANAGEMENT =============

    /**
     * Get all trips
     */
    public List<Trip> getAllTrips() {
        return JPAUtil.executeInReadOnly(em -> {
            String jpql = "SELECT t FROM Trip t " +
                    "JOIN FETCH t.route r " +
                    "JOIN FETCH r.startLocation " +
                    "JOIN FETCH r.endLocation " +
                    "JOIN FETCH t.bus " +
                    "ORDER BY t.departureTime DESC";
            return em.createQuery(jpql, Trip.class).getResultList();
        });
    }

    /**
     * Create new trip
     */
    public boolean createTrip(Long routeId, Long busId, OffsetDateTime departureTime, 
                             OffsetDateTime arrivalTime, BigDecimal pricePerSeat) {
        try {
            JPAUtil.executeInTransaction(em -> {
                Route route = em.find(Route.class, routeId);
                Bus bus = em.find(Bus.class, busId);

                if (route == null || bus == null) {
                    throw new RuntimeException("Route or Bus not found");
                }

                Trip trip = new Trip();
                trip.setRoute(route);
                trip.setBus(bus);
                trip.setDepartureTime(departureTime);
                trip.setArrivalTime(arrivalTime);
                trip.setPricePerSeat(pricePerSeat);

                em.persist(trip);
                logger.info("Trip created: {} to {}", 
                    route.getStartLocation().getName(), 
                    route.getEndLocation().getName());
            });
            return true;
        } catch (Exception e) {
            logger.error("Failed to create trip", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Update trip
     */
    public boolean updateTrip(Long tripId, OffsetDateTime departureTime, OffsetDateTime arrivalTime, 
                             BigDecimal pricePerSeat) {
        try {
            JPAUtil.executeInTransaction(em -> {
                Trip trip = em.find(Trip.class, tripId);
                if (trip == null) {
                    throw new RuntimeException("Trip not found");
                }

                trip.setDepartureTime(departureTime);
                trip.setArrivalTime(arrivalTime);
                trip.setPricePerSeat(pricePerSeat);

                em.merge(trip);
                logger.info("Trip updated: {}", tripId);
            });
            return true;
        } catch (Exception e) {
            logger.error("Failed to update trip", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Delete trip
     */
    public boolean deleteTrip(Long tripId) {
        try {
            JPAUtil.executeInTransaction(em -> {
                Trip trip = em.find(Trip.class, tripId);
                if (trip == null) {
                    throw new RuntimeException("Trip not found");
                }

                // Check if trip has bookings
                Long bookingCount = em.createQuery(
                    "SELECT COUNT(b) FROM Booking b WHERE b.trip.id = :tripId", Long.class)
                    .setParameter("tripId", tripId)
                    .getSingleResult();

                if (bookingCount > 0) {
                    throw new RuntimeException("Cannot delete trip with existing bookings. Cancel it instead.");
                }

                em.remove(trip);
                logger.info("Trip deleted: {}", tripId);
            });
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete trip", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    // ============= ROUTE MANAGEMENT =============

    /**
     * Get all routes
     */
    public List<Route> getAllRoutes() {
        return JPAUtil.executeInReadOnly(em -> {
            String jpql = "SELECT r FROM Route r " +
                    "JOIN FETCH r.startLocation " +
                    "JOIN FETCH r.endLocation " +
                    "ORDER BY r.id";
            return em.createQuery(jpql, Route.class).getResultList();
        });
    }

    /**
     * Get all locations
     */
    public List<Location> getAllLocations() {
        return JPAUtil.executeInReadOnly(em -> {
            String jpql = "SELECT l FROM Location l ORDER BY l.name";
            return em.createQuery(jpql, Location.class).getResultList();
        });
    }

    /**
     * Get all buses
     */
    public List<Bus> getAllBuses() {
        return JPAUtil.executeInReadOnly(em -> {
            String jpql = "SELECT b FROM Bus b ORDER BY b.licensePlate";
            return em.createQuery(jpql, Bus.class).getResultList();
        });
    }

    /**
     * Create new route
     */
    public boolean createRoute(Long startLocationId, Long endLocationId, Double distanceKm) {
        try {
            JPAUtil.executeInTransaction(em -> {
                Location startLocation = em.find(Location.class, startLocationId);
                Location endLocation = em.find(Location.class, endLocationId);

                if (startLocation == null || endLocation == null) {
                    throw new RuntimeException("Location not found");
                }

                if (startLocationId.equals(endLocationId)) {
                    throw new RuntimeException("Start and end locations must be different");
                }

                // Check if route already exists
                Long count = em.createQuery(
                    "SELECT COUNT(r) FROM Route r WHERE r.startLocation.id = :startId AND r.endLocation.id = :endId", 
                    Long.class)
                    .setParameter("startId", startLocationId)
                    .setParameter("endId", endLocationId)
                    .getSingleResult();

                if (count > 0) {
                    throw new RuntimeException("Route already exists");
                }

                Route route = new Route(startLocation, endLocation, distanceKm);
                em.persist(route);
                logger.info("Route created: {} to {}", startLocation.getName(), endLocation.getName());
            });
            return true;
        } catch (Exception e) {
            logger.error("Failed to create route", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Create new bus
     */
    public boolean createBus(String licensePlate, String model, Integer totalSeats, Integer manufactureYear) {
        try {
            JPAUtil.executeInTransaction(em -> {
                // Check if license plate already exists
                Long count = em.createQuery(
                    "SELECT COUNT(b) FROM Bus b WHERE b.licensePlate = :licensePlate", 
                    Long.class)
                    .setParameter("licensePlate", licensePlate)
                    .getSingleResult();

                if (count > 0) {
                    throw new RuntimeException("License plate already exists");
                }

                Bus bus = new Bus(licensePlate, model, totalSeats, manufactureYear);
                em.persist(bus);
                logger.info("Bus created: {}", licensePlate);
            });
            return true;
        } catch (Exception e) {
            logger.error("Failed to create bus", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    // ============= STATISTICS =============

    /**
     * Get booking statistics
     */
    public Object[] getBookingStats() {
        return JPAUtil.executeInReadOnly(em -> {
            Long totalBookings = em.createQuery("SELECT COUNT(b) FROM Booking b", Long.class)
                .getSingleResult();
            Long pendingBookings = em.createQuery(
                "SELECT COUNT(b) FROM Booking b WHERE b.status = 'Pending'", Long.class)
                .getSingleResult();
            Long confirmedBookings = em.createQuery(
                "SELECT COUNT(b) FROM Booking b WHERE b.status = 'Confirmed'", Long.class)
                .getSingleResult();
            
            return new Object[]{totalBookings, pendingBookings, confirmedBookings};
        });
    }

    /**
     * Get user statistics
     */
    public Object[] getUserStats() {
        return JPAUtil.executeInReadOnly(em -> {
            Long totalUsers = em.createQuery("SELECT COUNT(u) FROM User u", Long.class)
                .getSingleResult();
            Long activeUsers = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.isActive = true", Long.class)
                .getSingleResult();
            
            return new Object[]{totalUsers, activeUsers};
        });
    }
}
