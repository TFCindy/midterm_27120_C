package auca.ac.rw.carRentalHub.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import auca.ac.rw.carRentalHub.model.Feature;
import auca.ac.rw.carRentalHub.model.Vehicle;
import auca.ac.rw.carRentalHub.repository.FeatureRepository;
import auca.ac.rw.carRentalHub.repository.VehicleRepository;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private FeatureRepository featureRepository;

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Optional<Vehicle> getVehicle(UUID id) {
        return vehicleRepository.findById(id);
    }

    public Page<Vehicle> listVehicles(Pageable pageable) {
        return vehicleRepository.findAll(pageable);
    }

    @Transactional
    public Vehicle addFeatureToVehicle(UUID vehicleId, UUID featureId, java.math.BigDecimal ignoredAdditionalCost) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found"));

        vehicle.addFeature(feature);
        return vehicleRepository.save(vehicle);
    }

    public Page<Vehicle> findByFeature(UUID featureId, Pageable pageable) {
        return vehicleRepository.findByFeatureId(featureId, pageable);
    }
}
