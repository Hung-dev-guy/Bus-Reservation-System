package com.hungdev.busbookingsystem.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "seats",
    uniqueConstraints = @UniqueConstraint(name = "seats_bus_id_seat_number_5458c34b_uniq", columnNames = {"bus_id", "seat_number"}),
    indexes = @Index(name = "seats_bus_id_6ef4699d", columnList = "bus_id")
)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL)
    private List<Ticket> tickets;

    // Constructors
    public Seat() {
        this.isAvailable = true;
    }

    public Seat(String seatNumber, Bus bus) {
        this();
        this.seatNumber = seatNumber;
        this.bus = bus;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", seatNumber='" + seatNumber + '\'' +
                ", isAvailable=" + isAvailable +
                ", bus=" + (bus != null ? bus.getLicensePlate() : "null") +
                '}';
    }
}
