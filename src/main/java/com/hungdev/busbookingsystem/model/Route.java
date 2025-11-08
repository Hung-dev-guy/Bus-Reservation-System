package com.hungdev.busbookingsystem.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "routes",
    uniqueConstraints = @UniqueConstraint(name = "routes_start_location_id_end_location_id_47173a8c_uniq", columnNames = {"start_location_id", "end_location_id"}),
    indexes = {
        @Index(name = "routes_start_location_id_355e3cb1", columnList = "start_location_id"),
        @Index(name = "routes_end_location_id_887fe0af", columnList = "end_location_id")
    }
)
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "start_location_id", nullable = false)
    private Location startLocation;

    @ManyToOne
    @JoinColumn(name = "end_location_id", nullable = false)
    private Location endLocation;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;
    
    // Relationships
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<Trip> trips;
    
    // Constructors
    public Route() {}

    public Route(Location startLocation, Location endLocation, Double distanceKm) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.distanceKm = distanceKm;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
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
                ", distanceKm=" + distanceKm + " km" +
                '}';
    }
}