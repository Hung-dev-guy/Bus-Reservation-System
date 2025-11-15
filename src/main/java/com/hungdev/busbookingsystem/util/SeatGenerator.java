package com.hungdev.busbookingsystem.util;

import com.hungdev.busbookingsystem.model.Bus;
import com.hungdev.busbookingsystem.model.Seat;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to generate seats for all buses based on their total seat capacity.
 * Creates proper seat numbering layouts (e.g., A1, A2, B1, B2, etc.)
 */
public class SeatGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SeatGenerator.class);
    
    // Standard bus layouts
    private static final String[] ROWS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O"};
    
    public static void main(String[] args) {
        logger.info("Starting seat generation for all buses...");
        
        try {
            int totalSeats = generateSeatsForAllBuses();
            logger.info("Seat generation completed successfully!");
            logger.info("Total seats created: {}", totalSeats);
        } catch (Exception e) {
            logger.error("Error generating seats", e);
        } finally {
            JPAUtil.shutdown();
        }
    }

    /**
     * Generates seats for all buses in the database
     * @return Total number of seats created
     */
    public static int generateSeatsForAllBuses() {
        EntityManager em = JPAUtil.getEntityManager();
        int totalSeatsCreated = 0;
        
        try {
            // Get all buses
            List<Bus> buses = em.createQuery("SELECT b FROM Bus b ORDER BY b.id", Bus.class)
                    .getResultList();
            
            logger.info("Found {} buses to generate seats for", buses.size());
            
            for (Bus bus : buses) {
                // Check if seats already exist for this bus
                Long existingSeats = em.createQuery(
                        "SELECT COUNT(s) FROM Seat s WHERE s.bus.id = :busId", Long.class)
                        .setParameter("busId", bus.getId())
                        .getSingleResult();
                
                if (existingSeats > 0) {
                    logger.info("Bus {} already has {} seats, skipping...", 
                            bus.getLicensePlate(), existingSeats);
                    totalSeatsCreated += existingSeats.intValue();
                    continue;
                }
                
                // Generate seats for this bus
                int seatsCreated = generateSeatsForBus(em, bus);
                totalSeatsCreated += seatsCreated;
                
                logger.info("Created {} seats for bus {} ({})", 
                        seatsCreated, bus.getLicensePlate(), bus.getModel());
            }
            
            logger.info("Successfully created {} total seats for {} buses", 
                    totalSeatsCreated, buses.size());
            
        } catch (Exception e) {
            logger.error("Error in seat generation", e);
            throw new RuntimeException("Failed to generate seats", e);
        } finally {
            em.close();
        }
        
        return totalSeatsCreated;
    }

    /**
     * Generates seats for a specific bus based on its total seat capacity
     * @param em EntityManager
     * @param bus The bus to generate seats for
     * @return Number of seats created
     */
    private static int generateSeatsForBus(EntityManager em, Bus bus) {
        em.getTransaction().begin();
        
        try {
            int totalSeats = bus.getTotalSeats();
            List<Seat> seats = new ArrayList<>();
            
            // Determine layout based on seat count
            SeatLayout layout = determineSeatLayout(totalSeats);
            
            int seatNumber = 1;
            int seatsPerRow = layout.seatsPerRow;
            int rows = (int) Math.ceil((double) totalSeats / seatsPerRow);
            
            for (int row = 0; row < rows && seatNumber <= totalSeats; row++) {
                String rowLetter = ROWS[row % ROWS.length];
                
                for (int col = 1; col <= seatsPerRow && seatNumber <= totalSeats; col++) {
                    String seatCode = rowLetter + col;
                    
                    Seat seat = new Seat(seatCode, bus);
                    seat.setIsAvailable(true);
                    
                    em.persist(seat);
                    seats.add(seat);
                    
                    seatNumber++;
                }
            }
            
            em.getTransaction().commit();
            return seats.size();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error creating seats for bus {}", bus.getLicensePlate(), e);
            throw new RuntimeException("Failed to create seats for bus: " + bus.getLicensePlate(), e);
        }
    }

    /**
     * Determines the seat layout based on total seat capacity
     */
    private static SeatLayout determineSeatLayout(int totalSeats) {
        if (totalSeats <= 16) {
            // Minibus: 2+1 layout (3 seats per row)
            return new SeatLayout(3, "Minibus 2+1");
        } else if (totalSeats <= 30) {
            // Small bus: 2+2 layout (4 seats per row)
            return new SeatLayout(4, "Small Bus 2+2");
        } else if (totalSeats <= 45) {
            // Standard bus: 2+3 layout (5 seats per row)
            return new SeatLayout(5, "Standard Bus 2+3");
        } else {
            // Large bus: 3+3 layout (6 seats per row)
            return new SeatLayout(6, "Large Bus 3+3");
        }
    }

    /**
     * Helper class to represent seat layout configuration
     */
    private static class SeatLayout {
        int seatsPerRow;
        
        SeatLayout(int seatsPerRow, String description) {
            this.seatsPerRow = seatsPerRow;
            // description is for documentation purposes
        }
    }

    /**
     * Generate seats for a specific bus by ID
     * Useful for generating seats for a single bus
     */
    public static int generateSeatsForBusById(Long busId) {
        EntityManager em = JPAUtil.getEntityManager();
        
        try {
            Bus bus = em.find(Bus.class, busId);
            if (bus == null) {
                logger.error("Bus not found with ID: {}", busId);
                throw new IllegalArgumentException("Bus not found with ID: " + busId);
            }
            
            // Check if seats already exist
            Long existingSeats = em.createQuery(
                    "SELECT COUNT(s) FROM Seat s WHERE s.bus.id = :busId", Long.class)
                    .setParameter("busId", busId)
                    .getSingleResult();
            
            if (existingSeats > 0) {
                logger.warn("Bus {} already has {} seats", bus.getLicensePlate(), existingSeats);
                return existingSeats.intValue();
            }
            
            return generateSeatsForBus(em, bus);
            
        } finally {
            em.close();
        }
    }

    /**
     * Delete all seats for a specific bus
     * Useful for regenerating seats
     */
    public static void deleteSeatsForBus(Long busId) {
        EntityManager em = JPAUtil.getEntityManager();
        
        try {
            em.getTransaction().begin();
            
            int deletedCount = em.createQuery(
                    "DELETE FROM Seat s WHERE s.bus.id = :busId")
                    .setParameter("busId", busId)
                    .executeUpdate();
            
            em.getTransaction().commit();
            logger.info("Deleted {} seats for bus ID: {}", deletedCount, busId);
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error deleting seats for bus ID: {}", busId, e);
            throw new RuntimeException("Failed to delete seats", e);
        } finally {
            em.close();
        }
    }

    /**
     * Regenerate seats for a specific bus
     */
    public static int regenerateSeatsForBus(Long busId) {
        logger.info("Regenerating seats for bus ID: {}", busId);
        deleteSeatsForBus(busId);
        return generateSeatsForBusById(busId);
    }
}
