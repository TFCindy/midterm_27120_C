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
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import auca.ac.rw.carRentalHub.dto.LocationDTO;
import auca.ac.rw.carRentalHub.dto.LocationRequest;
import auca.ac.rw.carRentalHub.model.Location;
import auca.ac.rw.carRentalHub.service.LocationService;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    /**
     * ENDPOINT: POST /api/locations/save
     * BODY (DTO):
     * { "code": "RW-01", "name": "Kigali City", "type": "PROVINCE", "parentId": null }
     */
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LocationDTO> saveLocation(@RequestBody LocationRequest request) {

        Location location = new Location();
        location.setCode(request.getCode());
        location.setName(request.getName());
        location.setType(request.getType());

        Location saved = locationService.saveLocation(location, request.getParentId());

        LocationDTO dto = toDto(saved);
        return ResponseEntity.status(201).body(dto);
    }

    /**
     * ENDPOINT: GET /api/locations?page=0&size=10&sortBy=name&direction=ASC
     * This demonstrates sorting and pagination (Requirement #3)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("DESC") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Location> locationPage = locationService.getAllLocations(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("locations", locationPage.map(this::toDto).getContent());
        response.put("currentPage", locationPage.getNumber());
        response.put("totalItems", locationPage.getTotalElements());
        response.put("totalPages", locationPage.getTotalPages());
        response.put("pageSize", locationPage.getSize());
        response.put("sortBy", sortBy);
        response.put("sortDirection", direction);
        
        return ResponseEntity.ok(response);
    }

    /**
     * ENDPOINT: GET /api/locations/provinces
     * Get all provinces
     */
    @GetMapping("/provinces")
    public ResponseEntity<List<LocationDTO>> getAllProvinces() {
        List<LocationDTO> dtos = locationService.getAllProvinces()
                .stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * ENDPOINT: GET /api/locations/{id}/children
     * Get all children of a location
     */
    @GetMapping("/{id}/children")
    public ResponseEntity<List<LocationDTO>> getChildren(@PathVariable UUID id) {
        List<LocationDTO> dtos = locationService.getChildren(id)
                .stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}/descendants")
    public ResponseEntity<List<UUID>> getAllDescendants(@PathVariable UUID id) {
        return ResponseEntity.ok(locationService.getAllDescendantIds(id));
    }

    @GetMapping("/{id}/address")
    public ResponseEntity<Map<String, String>> getFullAddress(@PathVariable UUID id) {
        String address = locationService.getFullAddress(id);
        Map<String, String> response = new HashMap<>();
        response.put("address", address);
        return ResponseEntity.ok(response);
    }

    private LocationDTO toDto(Location location) {
        LocationDTO dto = new LocationDTO(location.getId(), location.getCode(), location.getName(),
                location.getType().name());
        if (location.getParent() != null) {
            dto.setParentId(location.getParent().getId().toString());
            dto.setParentName(location.getParent().getName());
        }
        return dto;
    }
}