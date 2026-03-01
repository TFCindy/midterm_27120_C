package auca.ac.rw.carRentalHub.model;

import java.util.UUID;
import java.math.BigDecimal;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * JOIN TABLE for Many-to-Many relationship between Vehicle and Feature
 * 
 * EXPLANATION:
 * - This is the join table that breaks the Many-to-Many into two One-to-Many relationships
 * - It contains:
 *   - id (PK): Surrogate primary key
 *   - vehicle_id (FK): References the Vehicle table
 *   - feature_id (FK): References the Feature table
 *   - additionalCost: Extra attribute specific to this relationship
 * 
 * Why use a join table entity instead of @ManyToMany?
 * 1. Allows adding extra attributes (additionalCost)
 * 2. Provides more control over the relationship
 * 3. Easier to query and modify
 * 4. Follows database normalization principles
 */
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