package auca.ac.rw.carRentalHub.model;

import java.util.UUID;
import java.math.BigDecimal;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

// Join entity for Vehicle <-> Feature with extra attribute
@Entity
@Table(name = "vehicle_feature", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"vehicle_id", "feature_id"}))
public class VehicleFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonIgnore
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

    private BigDecimal additionalCost;

    // Constructors
    public VehicleFeature() {}

    public VehicleFeature(Vehicle vehicle, Feature feature, BigDecimal additionalCost) {
        this.vehicle = vehicle;
        this.feature = feature;
        this.additionalCost = additionalCost;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public Feature getFeature() { return feature; }
    public void setFeature(Feature feature) { this.feature = feature; }

    public BigDecimal getAdditionalCost() { return additionalCost; }
    public void setAdditionalCost(BigDecimal additionalCost) { 
        this.additionalCost = additionalCost; 
    }

    // Helper to get feature name without loading entire feature
    public String getFeatureName() {
        return feature != null ? feature.getName() : null;
    }

    // Helper to get feature description
    public String getFeatureDescription() {
        return feature != null ? feature.getDescription() : null;
    }
}