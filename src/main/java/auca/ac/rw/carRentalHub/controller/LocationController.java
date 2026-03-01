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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import auca.ac.rw.carRentalHub.dto.LocationDTO;
import auca.ac.rw.carRentalHub.model.Location;
import auca.ac.rw.carRentalHub.service.LocationService;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    /**
     * ENDPOINT: POST /api/locations/save
     * BODY: {
     *   "code": "RW-01",
     *   "name": "Kigali City",
     *   "type": "PROVINCE"
     * }
     * 
     * For child locations, add parentId parameter:
     * POST /api/locations/save?parentId=123e4567-e89b-12d3-a456-426614174000
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> saveLocation(
            @RequestBody Location location,
            @RequestParam(required = false) String parentId) {
        
        Map<String, String> response = new HashMap<>();
        String result = locationService.saveLocation(location, parentId);
        
        if (result.contains("successfully")) {
            response.put("message", result);
            response.put("status", "success");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.put("message", result);
            response.put("status", "error");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
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
        response.put("locations", locationPage.getContent());
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
    public ResponseEntity<List<Location>> getAllProvinces() {
        return ResponseEntity.ok(locationService.getAllProvinces());
    }

    /**
     * ENDPOINT: GET /api/locations/{id}/children
     * Get all children of a location
     */
    @GetMapping("/{id}/children")
    public ResponseEntity<List<Location>> getChildren(@PathVariable UUID id) {
        return ResponseEntity.ok(locationService.getChildren(id));
    }
}