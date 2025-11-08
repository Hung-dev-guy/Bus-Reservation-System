package com.hungdev.busbookingsystem.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "buses",
    indexes = @Index(name = "buses_license_plate_b7203ba4_like", columnList = "license_plate")
)
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate", unique = true, nullable = false, length = 30)
    private String licensePlate;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "manufacture_year", nullable = false)
    private Integer manufactureYear;

    // Relationships
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL)
    private List<Trip> trips;

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL)
    private List<Seat> seats;
    
    // Constructors
    public Bus() {}

    public Bus(String licensePlate, String model, Integer totalSeats, Integer manufactureYear) {
        this.licensePlate = licensePlate;
        this.model = model;
        this.totalSeats = totalSeats;
        this.manufactureYear = manufactureYear;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Integer getTotalSeats() {
        return totalSeats;
    }
    
    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }
    
    public Integer getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(Integer manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Bus{" +
                "id=" + id +
                ", licensePlate='" + licensePlate + '\'' +
                ", model='" + model + '\'' +
                ", totalSeats=" + totalSeats +
                ", manufactureYear=" + manufactureYear +
                '}';
    }
}