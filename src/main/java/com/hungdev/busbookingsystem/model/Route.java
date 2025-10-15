package com.hungdev.busbookingsystem.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "start_location_id", nullable = false)
    private Location startLocation;
    
    @ManyToOne
    @JoinColumn(name = "end_location_id", nullable = false)
    private Location endLocation;
    
    @Column(nullable = false)
    private Integer distance;
    
    // Relationships
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<Trip> trips;
    
    // Constructors
    public Route() {}
    
    public Route(Location startLocation, Location endLocation, Integer distance) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.distance = distance;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Location getStartLocation() {
        return startLocation;
    }
    
    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }
    
    public Location getEndLocation() {
        return endLocation;
    }
    
    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }
    
    public Integer getDistance() {
        return distance;
    }
    
    public void setDistance(Integer distance) {
        this.distance = distance;
    }
    
    public List<Trip> getTrips() {
        return trips;
    }
    
    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }
    
    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", startLocation=" + startLocation.getName() +
                ", endLocation=" + endLocation.getName() +
                ", distance=" + distance + " km" +
                '}';
    }
}