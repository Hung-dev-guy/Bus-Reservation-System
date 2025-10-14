package com.hungdev.busbookingsystem.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "buses")
public class Bus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "license_plate", unique = true, nullable = false, length = 30)
    private String licensePlate;
    
    @Column(length = 100)
    private String model;
    
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;
    
    // Relationships
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL)
    private List<Trip> trips;
    
    // Constructors
    public Bus() {}
    
    public Bus(String licensePlate, String model, Integer totalSeats) {
        this.licensePlate = licensePlate;
        this.model = model;
        this.totalSeats = totalSeats;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
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
    
    public List<Trip> getTrips() {
        return trips;
    }
    
    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }
    
    @Override
    public String toString() {
        return "Bus{" +
                "id=" + id +
                ", licensePlate='" + licensePlate + '\'' +
                ", model='" + model + '\'' +
                ", totalSeats=" + totalSeats +
                '}';
    }
}