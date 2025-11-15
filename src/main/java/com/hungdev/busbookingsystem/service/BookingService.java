package com.hungdev.busbookingsystem.service;

import com.hungdev.busbookingsystem.model.Booking;
import com.hungdev.busbookingsystem.model.Seat;
import com.hungdev.busbookingsystem.model.Ticket;
import com.hungdev.busbookingsystem.model.Trip;
import com.hungdev.busbookingsystem.model.User;
import com.hungdev.busbookingsystem.util.JPAUtil;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for booking management
 */
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private static BookingService instance;

    private BookingService() {}

    public static BookingService getInstance() {
        if (instance == null) {
            synchronized (BookingService.class) {
                if (instance == null) {
                    instance = new BookingService();
                }
            }
        }
        return instance;
    }

    /**
     * Create a new booking with tickets
     */
    public Booking createBooking(User user, Trip trip, List<String> seatNumbers) {
        try {
            Booking[] createdBooking = new Booking[1];

            JPAUtil.executeInTransaction(em -> {
                // Refresh trip to get latest data
                Trip managedTrip = em.find(Trip.class, trip.getId());
                User managedUser = em.find(User.class, user.getId());

                // Calculate total amount
                BigDecimal totalAmount = trip.getPricePerSeat().multiply(BigDecimal.valueOf(seatNumbers.size()));

                // Create booking
                Booking booking = new Booking(managedUser, managedTrip, seatNumbers.size(), totalAmount);
                booking.setStatus("Pending");
                booking.setBookingTime(OffsetDateTime.now());

                // Create tickets for each seat
                List<Ticket> tickets = new ArrayList<>();
                for (String seatNumber : seatNumbers) {
                    // Find or create seat
                    Seat seat = em.createQuery(
                            "SELECT s FROM Seat s WHERE s.bus.id = :busId AND s.seatNumber = :seatNumber",
                            Seat.class)
                            .setParameter("busId", managedTrip.getBus().getId())
                            .setParameter("seatNumber", seatNumber)
                            .getResultStream()
                            .findFirst()
                            .orElseGet(() -> {
                                Seat newSeat = new Seat(seatNumber, managedTrip.getBus());
                                em.persist(newSeat);
                                return newSeat;
                            });

                    Ticket ticket = new Ticket();
                    ticket.setBooking(booking);
                    ticket.setTrip(managedTrip);
                    ticket.setSeat(seat);
                    ticket.setPassengerName(managedUser.getFirstName() + " " + managedUser.getLastName());
                    ticket.setPrice(trip.getPricePerSeat());
                    tickets.add(ticket);
                }
                booking.setTickets(tickets);

                em.persist(booking);
                createdBooking[0] = booking;

                logger.info("Booking created: {} for user: {}", booking.getId(), user.getUsername());
            });

            return createdBooking[0];
        } catch (Exception e) {
            logger.error("Failed to create booking", e);
            return null;
        }
    }

    /**
     * Get user's bookings
     */
    public List<Booking> getUserBookings(Long userId) {
        return JPAUtil.executeInReadOnly(em -> {
            String jpql = "SELECT DISTINCT b FROM Booking b " +
                    "JOIN FETCH b.trip t " +
                    "JOIN FETCH t.route r " +
                    "JOIN FETCH r.startLocation " +
                    "JOIN FETCH r.endLocation " +
                    "JOIN FETCH t.bus " +
                    "WHERE b.user.id = :userId " +
                    "ORDER BY b.bookingTime DESC";

            List<Booking> bookings = em.createQuery(jpql, Booking.class)
                    .setParameter("userId", userId)
                    .getResultList();
            
            // Fetch tickets and payments separately to avoid cartesian product
            if (!bookings.isEmpty()) {
                em.createQuery("SELECT DISTINCT b FROM Booking b " +
                        "LEFT JOIN FETCH b.tickets " +
                        "WHERE b IN :bookings", Booking.class)
                        .setParameter("bookings", bookings)
                        .getResultList();
                        
                em.createQuery("SELECT DISTINCT b FROM Booking b " +
                        "LEFT JOIN FETCH b.payments " +
                        "WHERE b IN :bookings", Booking.class)
                        .setParameter("bookings", bookings)
                        .getResultList();
            }
            
            return bookings;
        });
    }

    /**
     * Get booking by ID
     */
    public Booking getBookingById(Long bookingId) {
        return JPAUtil.executeInReadOnly(em -> {
            String jpql = "SELECT b FROM Booking b " +
                    "JOIN FETCH b.trip t " +
                    "JOIN FETCH t.route r " +
                    "JOIN FETCH r.origin " +
                    "JOIN FETCH r.destination " +
                    "JOIN FETCH t.bus " +
                    "LEFT JOIN FETCH b.tickets " +
                    "LEFT JOIN FETCH b.payments " +
                    "WHERE b.id = :bookingId";

            TypedQuery<Booking> query = em.createQuery(jpql, Booking.class);
            query.setParameter("bookingId", bookingId);

            return query.getSingleResult();
        });
    }

    /**
     * Update booking status
     */
    public boolean updateBookingStatus(Long bookingId, String status) {
        try {
            JPAUtil.executeInTransaction(em -> {
                Booking booking = em.find(Booking.class, bookingId);
                if (booking != null) {
                    booking.setStatus(status);
                    em.merge(booking);
                    logger.info("Booking {} status updated to: {}", bookingId, status);
                }
            });
            return true;
        } catch (Exception e) {
            logger.error("Failed to update booking status", e);
            return false;
        }
    }

    /**
     * Cancel booking
     */
    public boolean cancelBooking(Long bookingId) {
        return updateBookingStatus(bookingId, "Cancelled");
    }

    /**
     * Get booked seat numbers for a trip
     */
    public List<String> getBookedSeats(Long tripId) {
        return JPAUtil.executeInReadOnly(em -> {
            String jpql = "SELECT t.seat.seatNumber FROM Ticket t " +
                    "JOIN t.booking b " +
                    "WHERE b.trip.id = :tripId " +
                    "AND b.status != 'Cancelled'";

            TypedQuery<String> query = em.createQuery(jpql, String.class);
            query.setParameter("tripId", tripId);

            return query.getResultList();
        });
    }
}
