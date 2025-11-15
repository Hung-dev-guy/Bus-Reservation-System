package com.hungdev.busbookingsystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tickets",
    uniqueConstraints = @UniqueConstraint(name = "tickets_trip_id_seat_id_a241093c_uniq", columnNames = {"trip_id", "seat_id"}),
    indexes = {
        @Index(name = "tickets_booking_id_d9ce02d0", columnList = "booking_id"),
        @Index(name = "tickets_seat_id_e8dc5a36", columnList = "seat_id"),
        @Index(name = "tickets_trip_id_b68c10e7", columnList = "trip_id")
    }
)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "passenger_name", nullable = false, length = 100)
    private String passengerName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    // Constructors
    public Ticket() {}

    public Ticket(Booking booking, Trip trip, Seat seat, String passengerName, BigDecimal price) {
        this.booking = booking;
        this.trip = trip;
        this.seat = seat;
        this.passengerName = passengerName;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", passengerName='" + passengerName + '\'' +
                ", seat=" + (seat != null ? seat.getSeatNumber() : "null") +
                ", price=" + price +
                '}';
    }
}