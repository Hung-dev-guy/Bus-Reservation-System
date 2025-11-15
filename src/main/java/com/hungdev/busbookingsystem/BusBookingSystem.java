package com.hungdev.busbookingsystem;

import com.hungdev.busbookingsystem.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BusBookingSystem {

    public static void main(String[] args) {
        System.out.println("=== Bus Booking System Demo ===\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Step 1: Create Locations
        System.out.println("STEP 1: Creating Locations");
        System.out.println("---------------------------");
        Location haNoi = new Location("Ha Noi Bus Station", "Ha Noi");
        Location hoChiMinh = new Location("Ho Chi Minh Bus Station", "Ho Chi Minh");
        System.out.println("Created: " + haNoi.getName() + " in " + haNoi.getCity());
        System.out.println("Created: " + hoChiMinh.getName() + " in " + hoChiMinh.getCity());
        System.out.println();

        // Step 2: Create Route
        System.out.println("STEP 2: Creating Route");
        System.out.println("----------------------");
        Route route = new Route(haNoi, hoChiMinh, 1700.0); // Nhớ thêm các khoảng cách của các cặp tỉnh khác
        System.out.println("Created Route: " + route.getStartLocation().getCity() +
                         " -> " + route.getEndLocation().getCity() +
                         " (" + route.getDistanceKm() + " km)");
        System.out.println();

        // Step 3: Create Bus
        System.out.println("STEP 3: Creating Bus");
        System.out.println("--------------------");
        Bus bus = new Bus("29A-12345", "Hyundai Universe", 45, 2020);
        System.out.println("Created Bus: License Plate = " + bus.getLicensePlate() +
                         ", Model = " + bus.getModel() +
                         ", Total Seats = " + bus.getTotalSeats());
        System.out.println();

        // Step 4: Create Trip
        System.out.println("STEP 4: Creating Trip");
        System.out.println("---------------------");
        OffsetDateTime departureTime = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime arrivalTime = departureTime.plusHours(28);
        BigDecimal pricePerSeat = new BigDecimal("500000");

        Trip trip = new Trip(route, bus, departureTime, arrivalTime, pricePerSeat);
        trip.setBookings(new ArrayList<>());
        System.out.println("Created Trip:");
        System.out.println("  Route: " + route.getStartLocation().getCity() + " -> " + route.getEndLocation().getCity());
        System.out.println("  Departure: " + departureTime.format(formatter));
        System.out.println("  Arrival: " + arrivalTime.format(formatter));
        System.out.println("  Price per seat: " + pricePerSeat + " VND");
        System.out.println("  Available seats: " + trip.getAvailableSeats());
        System.out.println();

        // Step 5: Create User (Customer)
        System.out.println("STEP 5: Creating Customer User");
        System.out.println("-------------------------------");
        User customer = new User("johndoe", "john.doe@email.com", "John", "Doe", "hashed_password_123", "CUSTOMER");
        System.out.println("Created User: " + customer.getUsername() +
                         " (" + customer.getFirstName() + " " + customer.getLastName() + ")");
        System.out.println("  Email: " + customer.getEmail());
        System.out.println("  Role: " + customer.getRole());
        System.out.println("  Active: " + customer.getIsActive());
        System.out.println();

        // Step 6: Create Booking
        System.out.println("STEP 6: Creating Booking");
        System.out.println("------------------------");
        int numberOfSeats = 3;
        BigDecimal totalAmount = pricePerSeat.multiply(new BigDecimal(numberOfSeats));

        Booking booking = new Booking(customer, trip, numberOfSeats, totalAmount);
        booking.setTickets(new ArrayList<>());
        System.out.println("Created Booking:");
        System.out.println("  Customer: " + customer.getUsername());
        System.out.println("  Trip: " + route.getStartLocation().getCity() + " -> " + route.getEndLocation().getCity());
        System.out.println("  Booking Time: " + booking.getBookingTime().format(formatter));
        System.out.println("  Total Amount: " + booking.getTotalAmount() + " VND");
        System.out.println("  Status: " + booking.getStatus());
        System.out.println();

        // Step 7: Create Tickets
        System.out.println("STEP 7: Creating Tickets");
        System.out.println("------------------------");
        String[] seatNumbers = {"A1", "A2", "A3"};
        for (String seatNumber : seatNumbers) {
            // Create a seat for this demo (in real app, seats would be pre-created)
            Seat seat = new Seat(seatNumber, bus);
            String passengerName = customer.getFirstName() + " " + customer.getLastName();
            Ticket ticket = new Ticket(booking, trip, seat, passengerName, pricePerSeat);
            booking.getTickets().add(ticket);
            System.out.println("Created Ticket: Seat " + seatNumber + " - Price: " + pricePerSeat + " VND");
        }
        trip.getBookings().add(booking);
        System.out.println();

        // Step 8: Create Payment
        System.out.println("STEP 8: Processing Payment");
        System.out.println("---------------------------");
        Payment payment = new Payment(booking, totalAmount, "Credit Card");
        payment.setTransactionCode("TXN-" + System.currentTimeMillis());
        payment.setStatus("Succeeded");
        System.out.println("Payment Processed:");
        System.out.println("  Amount: " + payment.getAmount() + " VND");
        System.out.println("  Method: " + payment.getPaymentMethod());
        System.out.println("  Transaction Code: " + payment.getTransactionCode());
        System.out.println("  Status: " + payment.getStatus());
        System.out.println();

        // Step 9: Confirm Booking
        System.out.println("STEP 9: Confirming Booking");
        System.out.println("--------------------------");
        booking.setStatus("Confirmed");
        System.out.println("Booking Status Updated: " + booking.getStatus());
        System.out.println();

        // Step 10: Display Summary
        System.out.println("=== BOOKING SUMMARY ===");
        System.out.println("======================");
        System.out.println("Customer: " + customer.getFirstName() + " " + customer.getLastName() +
                         " (@" + customer.getUsername() + ")");
        System.out.println("Route: " + route.getStartLocation().getName() + " -> " + route.getEndLocation().getName());
        System.out.println("Distance: " + route.getDistanceKm() + " km");
        System.out.println("Bus: " + bus.getModel() + " (" + bus.getLicensePlate() + ")");
        System.out.println("Departure: " + trip.getDepartureTime().format(formatter));
        System.out.println("Arrival: " + trip.getArrivalTime().format(formatter));
        System.out.println("Seats Booked: " + booking.getTickets().size() + " (" +
                         String.join(", ", seatNumbers) + ")");
        System.out.println("Total Paid: " + payment.getAmount() + " VND");
        System.out.println("Payment Status: " + payment.getStatus());
        System.out.println("Booking Status: " + booking.getStatus());
        System.out.println("Available Seats Remaining: " + trip.getAvailableSeats());
        System.out.println();

    }
}
