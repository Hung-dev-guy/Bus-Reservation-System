package com.hungdev.busbookingsystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "bookings",
    indexes = {
        @Index(name = "bookings_trip_id_89bc46e9", columnList = "trip_id"),
        @Index(name = "bookings_user_id_6e734b08", columnList = "user_id")
    }
)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "number_of_seats", nullable = false)
    private Integer numberOfSeats;

    @Column(name = "booking_time", nullable = false)
    private OffsetDateTime bookingTime = OffsetDateTime.now();

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 10)
    private String status; // Pending, Confirmed, Cancelled
    
    // Relationships
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Payment> payments;
    
    // Constructors
    public Booking() {
        this.bookingTime = OffsetDateTime.now();
        this.status = "Pending";
    }

    public Booking(User user, Trip trip, Integer numberOfSeats, BigDecimal totalAmount) {
        this();
        this.user = user;
        this.trip = trip;
        this.numberOfSeats = numberOfSeats;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Trip getTrip() {
        return trip;
    }
    
    public void setTrip(Trip trip) {
        this.trip = trip;
    }
    
    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public OffsetDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(OffsetDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<Ticket> getTickets() {
        return tickets;
    }
    
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
    
    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
    
    // Business methods
    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setBooking(this);
    }
    
    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
        ticket.setBooking(null);
    }
    
    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", user=" + user.getUsername() +
                ", trip=" + trip.getId() +
                ", bookingTime=" + bookingTime +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                '}';
    }
}