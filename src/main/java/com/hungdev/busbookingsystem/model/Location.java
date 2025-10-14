package com.hungdev.busbookingsystem.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "locations")
public class Location {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(nullable = false, length = 255)
    private String city;
    
    // Relationships
    @OneToMany(mappedBy = "startLocation")
    private List<Route> routesAsStart;
    
    @OneToMany(mappedBy = "endLocation")
    private List<Route> routesAsEnd;
    
    // Constructors
    public Location() {}
    
    public Location(String name, String city) {
        this.name = name;
        this.city = city;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public List<Route> getRoutesAsStart() {
        return routesAsStart;
    }
    
    public void setRoutesAsStart(List<Route> routesAsStart) {
        this.routesAsStart = routesAsStart;
    }
    
    public List<Route> getRoutesAsEnd() {
        return routesAsEnd;
    }
    
    public void setRoutesAsEnd(List<Route> routesAsEnd) {
        this.routesAsEnd = routesAsEnd;
    }
    
    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}