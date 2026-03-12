package auca.ac.rw.carRentalHub.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import auca.ac.rw.carRentalHub.model.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    
    // find vehicles that have a given feature (by feature id)
    @org.springframework.data.jpa.repository.Query("SELECT v FROM Vehicle v JOIN v.features f WHERE f.id = :featureId")
    org.springframework.data.domain.Page<Vehicle> findByFeatureId(@org.springframework.data.repository.query.Param("featureId") UUID featureId, org.springframework.data.domain.Pageable pageable);

}