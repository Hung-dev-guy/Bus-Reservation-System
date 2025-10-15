package com.hungdev.busbookingsystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trips")
public class Trip {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
    
    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;
    
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;
    
    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;
    
    @Column(name = "price_per_seat", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerSeat;
    
    // Relationships
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Booking> bookings;
    
    // Constructors
    public Trip() {}
    
    public Trip(Route route, Bus bus, LocalDateTime departureTime, LocalDateTime arrivalTime, BigDecimal pricePerSeat) {
        this.route = route;
        this.bus = bus;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.pricePerSeat = pricePerSeat;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
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
    
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
    
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }
    
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(LocalDateTime arrivalTime) {
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