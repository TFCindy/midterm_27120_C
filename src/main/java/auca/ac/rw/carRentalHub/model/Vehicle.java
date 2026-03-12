package auca.ac.rw.carRentalHub.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import auca.ac.rw.carRentalHub.model.enums.EVehicleStatus;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String licensePlate;

    private String brand;
    private String model;
    private Integer year;
    private BigDecimal dailyRate;

    @Enumerated(EnumType.STRING)
    private EVehicleStatus status;

    // One-to-Many with reservations
    @OneToMany(mappedBy = "vehicle")
    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();

    // Many-to-Many with Feature using join table vehicle_features
    @ManyToMany
    @JoinTable(name = "vehicle_features",
            joinColumns = @JoinColumn(name = "vehicle_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id"))
    private Set<Feature> features = new HashSet<>();

    // Constructors
    public Vehicle() {}

    public Vehicle(String licensePlate, String brand, String model, BigDecimal dailyRate) {
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
        this.status = EVehicleStatus.AVAILABLE;
    }

    public void addFeature(Feature feature) {
        features.add(feature);
        feature.getVehicles().add(this);
    }

    public void removeFeature(Feature feature) {
        features.remove(feature);
        feature.getVehicles().remove(this);
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getDailyRate() { return dailyRate; }
    public void setDailyRate(BigDecimal dailyRate) { this.dailyRate = dailyRate; }

    public EVehicleStatus getStatus() { return status; }
    public void setStatus(EVehicleStatus status) { this.status = status; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { 
        this.reservations = reservations; 
    }

    public Set<Feature> getFeatures() { return features; }
    public void setFeatures(Set<Feature> features) { this.features = features; }
}