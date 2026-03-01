package auca.ac.rw.carRentalHub.repository;

import java.util.UUID;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import auca.ac.rw.carRentalHub.model.VehicleFeature;
import auca.ac.rw.carRentalHub.model.Vehicle;
import auca.ac.rw.carRentalHub.model.Feature;

@Repository
public interface VehicleFeatureRepository extends JpaRepository<VehicleFeature, UUID> {
    
    // Find all features for a specific vehicle
    List<VehicleFeature> findByVehicle(Vehicle vehicle);
    
    // Find all vehicles that have a specific feature
    List<VehicleFeature> findByFeature(Feature feature);
    
    // Find specific vehicle-feature combination
    VehicleFeature findByVehicleAndFeature(Vehicle vehicle, Feature feature);
    
    // Check if exists (for requirement #7)
    boolean existsByVehicleAndFeature(Vehicle vehicle, Feature feature);
    
    // Complex query: Find vehicles with ALL specified features
    @Query("SELECT vf.vehicle FROM VehicleFeature vf " +
           "WHERE vf.feature.id IN :featureIds " +
           "GROUP BY vf.vehicle.id " +
           "HAVING COUNT(DISTINCT vf.feature.id) = :featureCount")
    List<Vehicle> findVehiclesWithAllFeatures(@Param("featureIds") List<UUID> featureIds, 
                                              @Param("featureCount") long featureCount);
    
    // Calculate total additional cost for a vehicle's features
    @Query("SELECT SUM(vf.additionalCost) FROM VehicleFeature vf " +
           "WHERE vf.vehicle.id = :vehicleId")
    BigDecimal calculateTotalFeaturesCost(@Param("vehicleId") UUID vehicleId);
    
    // Delete all features for a vehicle
    void deleteByVehicle(Vehicle vehicle);
}