package com.hungdev.busbookingsystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "trips",
    indexes = {
        @Index(name = "trips_route_id_715fed1b", columnList = "route_id"),
        @Index(name = "trips_bus_id_0f292d2d", columnList = "bus_id")
    }
)
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @Column(name = "departure_time", nullable = false)
    private OffsetDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private OffsetDateTime arrivalTime;

    @Column(name = "price_per_seat", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerSeat;
    
    // Relationships
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Booking> bookings;
    
    // Constructors
    public Trip() {}

    public Trip(Route route, Bus bus, OffsetDateTime departureTime, OffsetDateTime arrivalTime, BigDecimal pricePerSeat) {
        this.route = route;
        this.bus = bus;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.pricePerSeat = pricePerSeat;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Route getRoute() {
        return route;
    }
    
    public void setRoute(Route route) {
        this.route = route;
    }
    
    public Bus getBus() {
        return bus;
    }
    
    public void setBus(Bus bus) {
        this.bus = bus;
    }
    
    public OffsetDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(OffsetDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public OffsetDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(OffsetDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public BigDecimal getPricePerSeat() {
        return pricePerSeat;
    }
    
    public void setPricePerSeat(BigDecimal pricePerSeat) {
        this.pricePerSeat = pricePerSeat;
    }
    
    public List<Booking> getBookings() {
        return bookings;
    }
    
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
    
    // Business method: kiểm tra còn chỗ không
    public int getAvailableSeats() {
        int bookedSeats = 0;
        if (bookings != null) {
            for (Booking booking : bookings) {
                if (!"Cancelled".equals(booking.getStatus())) {
                    bookedSeats += booking.getTickets().size();
                }
            }
        }
        return bus.getTotalSeats() - bookedSeats;
    }
    
    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", route=" + route +
                ", bus=" + bus.getLicensePlate() +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", pricePerSeat=" + pricePerSeat +
                '}';
    }
}