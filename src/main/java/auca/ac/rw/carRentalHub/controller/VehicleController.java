package auca.ac.rw.carRentalHub.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import auca.ac.rw.carRentalHub.dto.VehicleDTO;
import auca.ac.rw.carRentalHub.dto.VehicleRequest;
import auca.ac.rw.carRentalHub.model.Vehicle;
import auca.ac.rw.carRentalHub.service.VehicleService;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDTO> saveVehicle(@RequestBody VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setDailyRate(request.getDailyRate());
        if (request.getStatus() != null) {
            vehicle.setStatus(request.getStatus());
        }

        Vehicle saved = vehicleService.saveVehicle(vehicle);
        return ResponseEntity.status(201).body(toDto(saved));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDTO> getVehicle(@PathVariable UUID id) {
        return vehicleService.getVehicle(id)
                .map(v -> ResponseEntity.ok(toDto(v)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> listVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "licensePlate") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Vehicle> result = vehicleService.listVehicles(pageable);
        List<VehicleDTO> content = result.map(this::toDto).getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("currentPage", result.getNumber());
        response.put("totalPages", result.getTotalPages());
        response.put("totalItems", result.getTotalElements());
        response.put("pageSize", result.getSize());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{vehicleId}/features/{featureId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDTO> addFeature(
            @PathVariable UUID vehicleId,
            @PathVariable UUID featureId,
            @RequestParam(required = false) java.math.BigDecimal additionalCost) {
        Vehicle updated = vehicleService.addFeatureToVehicle(vehicleId, featureId, additionalCost);
        return ResponseEntity.ok(toDto(updated));
    }

    @GetMapping(value = "/search/features", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> searchByFeature(
            @RequestParam UUID featureId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> result = vehicleService.findByFeature(featureId, pageable);
        List<VehicleDTO> content = result.map(this::toDto).getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("currentPage", result.getNumber());
        response.put("totalPages", result.getTotalPages());
        response.put("totalItems", result.getTotalElements());
        return ResponseEntity.ok(response);
    }

    private VehicleDTO toDto(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setYear(vehicle.getYear());
        dto.setDailyRate(vehicle.getDailyRate());
        dto.setStatus(vehicle.getStatus());
        return dto;
    }
}

