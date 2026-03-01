package auca.ac.rw.carRentalHub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import auca.ac.rw.carRentalHub.model.*;
import auca.ac.rw.carRentalHub.repository.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
public class PaginationDemoController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * DEMONSTRATION OF SORTING AND PAGINATION
     * 
     * EXPLANATION:
     * 
     * 1. HOW SORTING WORKS:
     *    - Spring Data JPA provides the Sort class
     *    - Sort.by("fieldName").ascending() or .descending()
     *    - Converts to SQL: ORDER BY field_name ASC/DESC
     * 
     * 2. HOW PAGINATION WORKS:
     *    - Pageable interface abstracts pagination logic
     *    - PageRequest.of(page, size, sort) creates pagination request
     *    - Database uses LIMIT and OFFSET (PostgreSQL) or similar
     *    - Example SQL: SELECT * FROM users ORDER BY name LIMIT 10 OFFSET 20
     * 
     * 3. PERFORMANCE BENEFITS:
     *    - Reduces network payload (only send 10 records instead of 1000)
     *    - Decreases database load (queries are smaller)
     *    - Faster response times
     *    - Better user experience (progressive loading)
     *    - Prevents memory exhaustion on server
     */

    @GetMapping("/users/paged")
    public Map<String, Object> getUsersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        
        // Create Sort object
        Sort sort = direction.equalsIgnoreCase("DESC") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        // Create Pageable object (combines page, size, and sort)
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Get paginated result
        Page<User> userPage = userRepository.findAll(pageable);
        
        // Build response with pagination metadata
        Map<String, Object> response = new HashMap<>();
        response.put("content", userPage.getContent());           // Actual data
        response.put("currentPage", userPage.getNumber());        // Current page number
        response.put("totalPages", userPage.getTotalPages());     // Total pages available
        response.put("totalItems", userPage.getTotalElements());  // Total records in database
        response.put("pageSize", userPage.getSize());             // Items per page
        response.put("hasNext", userPage.hasNext());              // Is there next page?
        response.put("hasPrevious", userPage.hasPrevious());      // Is there previous page?
        response.put("sortBy", sortBy);
        response.put("sortDirection", direction);
        
        // Add explanation for the assignment
        response.put("explanation", 
            "Pagination divides data into pages (size=" + size + "). " +
            "Sorting orders by " + sortBy + " in " + direction + " order. " +
            "Performance improves because only " + size + " records are transferred instead of all " + 
            userPage.getTotalElements() + " records.");
        
        return response;
    }

    @GetMapping("/vehicles/paged")
    public Map<String, Object> getVehiclesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "brand") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("DESC") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Vehicle> vehiclePage = vehicleRepository.findAll(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", vehiclePage.getContent());
        response.put("currentPage", vehiclePage.getNumber());
        response.put("totalPages", vehiclePage.getTotalPages());
        response.put("totalItems", vehiclePage.getTotalElements());
        response.put("pageSize", vehiclePage.getSize());
        
        return response;
    }

    /**
     * Demo of custom paginated query
     */
    @GetMapping("/reservations/by-status")
    public Page<Reservation> getReservationsByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("pickupDate").descending());
        return reservationRepository.findByStatus(status, pageable);
    }
}