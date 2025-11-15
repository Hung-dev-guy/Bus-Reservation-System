package com.hungdev.busbookingsystem.util;

import com.hungdev.busbookingsystem.model.Bus;
import com.hungdev.busbookingsystem.model.Location;
import com.hungdev.busbookingsystem.model.Route;
import com.hungdev.busbookingsystem.model.Trip;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class to generate 100 sample trips with routes, locations, and buses
 * for testing and development purposes.
 */
public class SampleDataGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SampleDataGenerator.class);
    private static final Random random = new Random();

    // Vietnamese cities and their bus stations
    private static final String[][] LOCATIONS = {
        {"Hanoi Central Station", "Hanoi"},
        {"Hanoi My Dinh Station", "Hanoi"},
        {"Hanoi Giap Bat Station", "Hanoi"},
        {"Ho Chi Minh Ben Xe Mien Dong", "Ho Chi Minh City"},
        {"Ho Chi Minh Ben Xe Mien Tay", "Ho Chi Minh City"},
        {"Ho Chi Minh An Suong Station", "Ho Chi Minh City"},
        {"Da Nang Central Station", "Da Nang"},
        {"Da Nang East Station", "Da Nang"},
        {"Hai Phong Tam Bac Station", "Hai Phong"},
        {"Hai Phong Lacuna Station", "Hai Phong"},
        {"Can Tho Central Station", "Can Tho"},
        {"Can Tho Nguyen Trai Station", "Can Tho"},
        {"Nha Trang Central Station", "Nha Trang"},
        {"Nha Trang North Station", "Nha Trang"},
        {"Hue Central Station", "Hue"},
        {"Hue An Cuu Station", "Hue"},
        {"Vung Tau Central Station", "Vung Tau"},
        {"Dalat Central Station", "Dalat"},
        {"Quy Nhon Station", "Quy Nhon"},
        {"Buon Ma Thuot Station", "Buon Ma Thuot"},
        {"Phan Thiet Station", "Phan Thiet"},
        {"Ha Long Station", "Ha Long"},
        {"Vinh Central Station", "Vinh"},
        {"Thanh Hoa Station", "Thanh Hoa"},
        {"Nam Dinh Station", "Nam Dinh"}
    };

    // Bus models
    private static final String[] BUS_MODELS = {
        "Hyundai Universe", "Thaco Mobihome", "Samco Primas",
        "Hino RN8J", "Isuzu Samco", "Mercedes-Benz Travego",
        "Thaco TB120S", "Daewoo FX120", "Hyundai Aero",
        "King Long XMQ6127"
    };

    // Seat configurations
    private static final int[] SEAT_COUNTS = {30, 34, 40, 44, 45, 50};

    public static void main(String[] args) {
        logger.info("Starting sample data generation...");
        
        try {
            generateSampleData();
            logger.info("Sample data generation completed successfully!");
        } catch (Exception e) {
            logger.error("Error generating sample data", e);
        } finally {
            JPAUtil.shutdown();
        }
    }

    /**
     * Generates 100 sample trips with all required dependencies
     */
    public static void generateSampleData() {
        EntityManager em = JPAUtil.getEntityManager();
        
        try {
            em.getTransaction().begin();
            
            logger.info("Creating locations...");
            List<Location> locations = createLocations(em);
            
            logger.info("Creating buses...");
            List<Bus> buses = createBuses(em, 30);
            
            logger.info("Creating routes...");
            List<Route> routes = createRoutes(em, locations, 40);
            
            logger.info("Creating 100 trips...");
            List<Trip> trips = createTrips(em, routes, buses, 100);
            
            em.getTransaction().commit();
            
            logger.info("Successfully created:");
            logger.info("  - {} locations", locations.size());
            logger.info("  - {} buses", buses.size());
            logger.info("  - {} routes", routes.size());
            logger.info("  - {} trips", trips.size());
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error in transaction", e);
            throw new RuntimeException("Failed to generate sample data", e);
        } finally {
            em.close();
        }
    }

    /**
     * Creates location entries from predefined data
     */
    private static List<Location> createLocations(EntityManager em) {
        List<Location> locations = new ArrayList<>();
        
        for (String[] locationData : LOCATIONS) {
            Location location = new Location(locationData[0], locationData[1]);
            em.persist(location);
            locations.add(location);
        }
        
        em.flush(); // Ensure IDs are generated
        return locations;
    }

    /**
     * Creates bus entries with various models and configurations
     */
    private static List<Bus> createBuses(EntityManager em, int count) {
        List<Bus> buses = new ArrayList<>();
        
        for (int i = 1; i <= count; i++) {
            String licensePlate = String.format("29B-%05d", 10000 + i);
            String model = BUS_MODELS[random.nextInt(BUS_MODELS.length)];
            int totalSeats = SEAT_COUNTS[random.nextInt(SEAT_COUNTS.length)];
            int manufactureYear = 2018 + random.nextInt(7); // 2018-2024
            
            Bus bus = new Bus(licensePlate, model, totalSeats, manufactureYear);
            em.persist(bus);
            buses.add(bus);
        }
        
        em.flush();
        return buses;
    }

    /**
     * Creates route entries connecting different locations
     */
    private static List<Route> createRoutes(EntityManager em, List<Location> locations, int count) {
        List<Route> routes = new ArrayList<>();
        
        // Create diverse routes
        for (int i = 0; i < count; i++) {
            Location start = locations.get(random.nextInt(locations.size()));
            Location end = locations.get(random.nextInt(locations.size()));
            
            // Ensure start and end are different
            while (end.getId().equals(start.getId())) {
                end = locations.get(random.nextInt(locations.size()));
            }
            
            // Calculate realistic distance based on cities
            double distance = calculateDistance(start.getCity(), end.getCity());
            
            // Check if route already exists
            final Location finalEnd = end;
            boolean routeExists = routes.stream()
                .anyMatch(r -> r.getStartLocation().getId().equals(start.getId()) 
                            && r.getEndLocation().getId().equals(finalEnd.getId()));
            
            if (!routeExists) {
                Route route = new Route(start, end, distance);
                em.persist(route);
                routes.add(route);
            }
        }
        
        em.flush();
        return routes;
    }

    /**
     * Creates 100 trip entries with varied schedules and pricing
     */
    private static List<Trip> createTrips(EntityManager em, List<Route> routes, 
                                          List<Bus> buses, int count) {
        List<Trip> trips = new ArrayList<>();
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        
        for (int i = 0; i < count; i++) {
            Route route = routes.get(random.nextInt(routes.size()));
            Bus bus = buses.get(random.nextInt(buses.size()));
            
            // Generate departure time (next 30 days)
            int daysAhead = random.nextInt(30);
            int hour = 6 + random.nextInt(18); // 6 AM to 11 PM
            int minute = random.nextInt(4) * 15; // 0, 15, 30, 45
            
            OffsetDateTime departureTime = now
                .plusDays(daysAhead)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);
            
            // Calculate arrival time based on distance (assume 60 km/h average)
            double distanceKm = route.getDistanceKm();
            long travelMinutes = (long) ((distanceKm / 60.0) * 60);
            OffsetDateTime arrivalTime = departureTime.plusMinutes(travelMinutes);
            
            // Calculate price based on distance (10,000 VND per km with variation)
            BigDecimal basePrice = BigDecimal.valueOf(distanceKm * 10000);
            BigDecimal variation = BigDecimal.valueOf(random.nextInt(50000) - 25000);
            BigDecimal pricePerSeat = basePrice.add(variation);
            
            // Ensure minimum price of 50,000 VND
            if (pricePerSeat.compareTo(BigDecimal.valueOf(50000)) < 0) {
                pricePerSeat = BigDecimal.valueOf(50000);
            }
            
            Trip trip = new Trip(route, bus, departureTime, arrivalTime, pricePerSeat);
            em.persist(trip);
            trips.add(trip);
            
            // Log progress every 20 trips
            if ((i + 1) % 20 == 0) {
                logger.info("Created {} trips...", i + 1);
            }
        }
        
        em.flush();
        return trips;
    }

    /**
     * Calculates approximate distance between cities in Vietnam
     */
    private static double calculateDistance(String city1, String city2) {
        // Simplified distance calculation based on common routes
        if (city1.equals(city2)) {
            return 10.0 + random.nextDouble() * 20.0; // Short local route
        }
        
        // Major city pairs with approximate distances (km)
        if (isCityPair(city1, city2, "Hanoi", "Ho Chi Minh City")) return 1700.0;
        if (isCityPair(city1, city2, "Hanoi", "Da Nang")) return 760.0;
        if (isCityPair(city1, city2, "Hanoi", "Hue")) return 660.0;
        if (isCityPair(city1, city2, "Hanoi", "Hai Phong")) return 120.0;
        if (isCityPair(city1, city2, "Hanoi", "Vinh")) return 290.0;
        if (isCityPair(city1, city2, "Ho Chi Minh City", "Da Nang")) return 960.0;
        if (isCityPair(city1, city2, "Ho Chi Minh City", "Nha Trang")) return 440.0;
        if (isCityPair(city1, city2, "Ho Chi Minh City", "Can Tho")) return 170.0;
        if (isCityPair(city1, city2, "Ho Chi Minh City", "Vung Tau")) return 125.0;
        if (isCityPair(city1, city2, "Ho Chi Minh City", "Dalat")) return 300.0;
        if (isCityPair(city1, city2, "Da Nang", "Hue")) return 100.0;
        if (isCityPair(city1, city2, "Da Nang", "Nha Trang")) return 540.0;
        
        // Default: random distance for other city pairs
        return 100.0 + random.nextDouble() * 500.0;
    }

    /**
     * Helper method to check if two cities match a pair (order-independent)
     */
    private static boolean isCityPair(String c1, String c2, String city1, String city2) {
        return (c1.equals(city1) && c2.equals(city2)) || (c1.equals(city2) && c2.equals(city1));
    }
}
