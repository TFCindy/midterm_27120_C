package auca.ac.rw.carRentalHub.model;

import java.util.UUID;
import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    // One-to-Many with VehicleFeature (join table for Many-to-Many with Feature)
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleFeature> features = new ArrayList<>();

    // Constructors
    public Vehicle() {}

    public Vehicle(String licensePlate, String brand, String model, BigDecimal dailyRate) {
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
        this.status = EVehicleStatus.AVAILABLE;
    }

    /**
     * Helper method to add a feature to this vehicle
     * This maintains both sides of the relationship
     */
    public void addFeature(Feature feature, BigDecimal additionalCost) {
        VehicleFeature vehicleFeature = new VehicleFeature();
        vehicleFeature.setVehicle(this);
        vehicleFeature.setFeature(feature);
        vehicleFeature.setAdditionalCost(additionalCost);
        features.add(vehicleFeature);
        feature.getVehicleFeatures().add(vehicleFeature);
    }

    /**
     * Helper method to remove a feature from this vehicle
     */
    public void removeFeature(Feature feature) {
        features.removeIf(vf -> vf.getFeature().equals(feature));
        feature.getVehicleFeatures().removeIf(vf -> vf.getVehicle().equals(this));
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

    public List<VehicleFeature> getFeatures() { return features; }
    public void setFeatures(List<VehicleFeature> features) { this.features = features; }
}