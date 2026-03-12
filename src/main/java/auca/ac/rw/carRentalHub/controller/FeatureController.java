package auca.ac.rw.carRentalHub.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import auca.ac.rw.carRentalHub.dto.FeatureDTO;
import auca.ac.rw.carRentalHub.dto.FeatureRequest;
import auca.ac.rw.carRentalHub.model.Feature;
import auca.ac.rw.carRentalHub.repository.FeatureRepository;

@RestController
@RequestMapping("/api/features")
public class FeatureController {

    @Autowired
    private FeatureRepository featureRepository;

    @PostMapping
    public ResponseEntity<FeatureDTO> create(@RequestBody FeatureRequest request) {
        Feature feature = new Feature();
        feature.setName(request.getName());
        feature.setDescription(request.getDescription());
        Feature saved = featureRepository.save(feature);
        return ResponseEntity.status(201).body(toDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<FeatureDTO>> list() {
        List<FeatureDTO> features = featureRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(features);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeatureDTO> get(@PathVariable UUID id) {
        return featureRepository.findById(id)
                .map(f -> ResponseEntity.ok(toDto(f)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeatureDTO> update(@PathVariable UUID id, @RequestBody FeatureRequest request) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found"));
        feature.setName(request.getName());
        feature.setDescription(request.getDescription());
        Feature saved = featureRepository.save(feature);
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!featureRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        featureRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private FeatureDTO toDto(Feature feature) {
        FeatureDTO dto = new FeatureDTO();
        dto.setId(feature.getId());
        dto.setName(feature.getName());
        dto.setDescription(feature.getDescription());
        return dto;
    }
}

