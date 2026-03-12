package auca.ac.rw.carRentalHub.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import auca.ac.rw.carRentalHub.dto.UserCreateRequest;
import auca.ac.rw.carRentalHub.dto.UserDTO;
import auca.ac.rw.carRentalHub.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * ENDPOINT: GET /api/users/by-province?identifier=Kigali
     * ENDPOINT: GET /api/users/by-province?identifier=RW-01
     * 
     * This endpoint implements REQUIREMENT #8
     * It retrieves all users from a province using either province code OR province
     * name
     */
    @GetMapping("/by-province")
    public ResponseEntity<Map<String, Object>> getUsersByProvince(
            @RequestParam String identifier) {

        List<UserDTO> users = userService.getUsersByProvince(identifier);

        Map<String, Object> response = new HashMap<>();
        response.put("provinceIdentifier", identifier);
        response.put("totalUsers", users.size());
        response.put("users", users);

        // Add explanation for the assignment
        response.put("explanation",
                "Found " + users.size() + " users in province '" + identifier + "'. " +
                        "The query searches for all users whose location is the province itself " +
                        "or any district/sector/cell/village within that province using the " +
                        "location hierarchy (parent-child relationship).");

        return ResponseEntity.ok(response);
    }

    /**
     * ENDPOINT: GET /api/users/by-location?identifier=...
     * Returns users attached to any location (province/district/sector/cell/village)
     * and all its descendants.
     */
    @GetMapping("/by-location")
    public ResponseEntity<Map<String, Object>> getUsersByLocation(
            @RequestParam String identifier) {

        List<UserDTO> users = userService.getUsersByLocation(identifier);

        Map<String, Object> response = new HashMap<>();
        response.put("locationIdentifier", identifier);
        response.put("totalUsers", users.size());
        response.put("users", users);

        return ResponseEntity.ok(response);
    }

    /**
     * ENDPOINT: GET /api/users
     * Supports pagination and sorting: page,size,sortBy,direction
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        org.springframework.data.domain.Sort sort = direction.equalsIgnoreCase("DESC")
                ? org.springframework.data.domain.Sort.by(sortBy).descending()
                : org.springframework.data.domain.Sort.by(sortBy).ascending();

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                sort);

        org.springframework.data.domain.Page<auca.ac.rw.carRentalHub.dto.UserDTO> pageResult = userService
                .getAllUsers(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("users", pageResult.getContent());
        response.put("currentPage", pageResult.getNumber());
        response.put("totalItems", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        response.put("pageSize", pageResult.getSize());
        response.put("sortBy", sortBy);
        response.put("sortDirection", direction);

        return ResponseEntity.ok(response);
    }

    /**
     * ENDPOINT: POST /api/users
     * Creates a user using ONLY village code or name (villageIdentifier).
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateRequest request) {
        UserDTO created = userService.createUser(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Test endpoint to check if a user exists by username
     * Demonstrates existsBy() method
     */
    @GetMapping("/check-exists")
    public ResponseEntity<Map<String, Boolean>> checkUserExists(
            @RequestParam String username) {
        // Call service to check username existence
        boolean exists = userService.existsByUsername(username);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }
}