package auca.ac.rw.carRentalHub.service;

import java.util.UUID;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import auca.ac.rw.carRentalHub.model.Vehicle;
import auca.ac.rw.carRentalHub.model.Feature;
import auca.ac.rw.carRentalHub.model.VehicleFeature;
import auca.ac.rw.carRentalHub.repository.VehicleRepository;
import auca.ac.rw.carRentalHub.repository.FeatureRepository;

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
    public Vehicle addFeatureToVehicle(UUID vehicleId, UUID featureId, java.math.BigDecimal additionalCost) {
        Optional<Vehicle> opt = vehicleRepository.findById(vehicleId);
        Optional<Feature> optF = featureRepository.findById(featureId);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Vehicle not found");
        }
        if (optF.isEmpty()) {
            throw new IllegalArgumentException("Feature not found");
        }
        Vehicle vehicle = opt.get();
        Feature feature = optF.get();
        vehicle.addFeature(feature, additionalCost == null ? java.math.BigDecimal.ZERO : additionalCost);
        return vehicleRepository.save(vehicle);
    }

    public Page<Vehicle> findByFeature(UUID featureId, Pageable pageable) {
        return vehicleRepository.findByFeatureId(featureId, pageable);
    }
}
