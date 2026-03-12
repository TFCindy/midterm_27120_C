package auca.ac.rw.carRentalHub.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import auca.ac.rw.carRentalHub.model.Location;
import auca.ac.rw.carRentalHub.model.enums.ELocationType;
import auca.ac.rw.carRentalHub.repository.LocationRepository;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    /**
     * Save a location with parent relationship
     * This implements requirement #2: Saving Location
     * 
     * EXPLANATION:
     * - The data is stored in the "location" table
     * - Parent relationship is handled via parent_id foreign key
     * - If parentId is provided, we find the parent location and set it
     * - This creates the hierarchy: Province → District → Sector → Cell → Village
     */
    @Transactional
    public String saveLocation(Location location, String parentId) {
        // keep old behavior for backward compatibility
        return saveChildAndParent(location, parentId);
    }

    /**
     * Method signature matching example service name. Does the same work.
     */
    @Transactional
    public String saveChildAndParent(Location location, String parentId) {
        try {
            // Check if location with same code already exists
            if (locationRepository.existsByCode(location.getCode())) {
                return "Location with code '" + location.getCode() + "' already exists!";
            }

            // Handle parent relationship if parentId is provided
            if (parentId != null && !parentId.isEmpty()) {
                UUID parentUUID = UUID.fromString(parentId);
                Location parent = locationRepository.findById(parentUUID).orElse(null);
                
                if (parent != null) {
                    // Validate hierarchy (e.g., District can only have Province as parent)
                    if (!isValidHierarchy(location.getType(), parent.getType())) {
                        return "Invalid hierarchy: A " + location.getType() + 
                               " cannot have a " + parent.getType() + " as parent!";
                    }
                    location.setParent(parent);
                } else {
                    return "Parent location with ID '" + parentId + "' not found!";
                }
            } else {
                // If no parent, this should be a PROVINCE (top level)
                if (location.getType() != ELocationType.PROVINCE) {
                    return "Only PROVINCE can have no parent. " + 
                           location.getType() + " must have a parent!";
                }
                location.setParent(null);
            }

            // Save the location
            locationRepository.save(location);
            
            return String.format("Location '%s' (%s) saved successfully!", 
                   location.getName(), location.getType());
            
        } catch (IllegalArgumentException e) {
            return "Invalid UUID format for parentId: " + parentId;
        } catch (Exception e) {
            return "Error saving location: " + e.getMessage();
        }
    }

    /**
     * Validate the hierarchy based on location types
     */
    private boolean isValidHierarchy(ELocationType childType, ELocationType parentType) {
        if (childType == ELocationType.DISTRICT) {
            return parentType == ELocationType.PROVINCE;
        } else if (childType == ELocationType.SECTOR) {
            return parentType == ELocationType.DISTRICT;
        } else if (childType == ELocationType.CELL) {
            return parentType == ELocationType.SECTOR;
        } else if (childType == ELocationType.VILLAGE) {
            return parentType == ELocationType.CELL;
        }
        return false;
    }

    /**
     * Get all locations with sorting and pagination
     * This implements part of requirement #3
     */
    public Page<Location> getAllLocations(Pageable pageable) {
        return locationRepository.findAll(pageable);
    }

    /**
     * Get all provinces
     */
    public List<Location> getAllProvinces() {
        return locationRepository.findByTypeOrderByNameAsc(ELocationType.PROVINCE);
    }

    /**
     * Get children of a location
     */
    public List<Location> getChildren(UUID parentId) {
        return locationRepository.findChildrenByParentId(parentId);
    }

    /**
     * Get all descendant locations for a given parent (including the parent itself).
     * Uses a recursive CTE in the repository to efficiently traverse the hierarchy.
     */
    public List<UUID> getAllDescendantIds(UUID parentId) {
        return locationRepository.findAllDescendantIds(parentId);
    }

    /**
     * Build a human-readable full address by walking up the parent chain.
     */
    public String getFullAddress(UUID locationId) {
        Location location = locationRepository.findById(locationId).orElse(null);
        if (location == null) {
            return "Location not found";
        }

        StringBuilder address = new StringBuilder();
        address.append(location.getName());

        Location current = location.getParent();
        while (current != null) {
            address.insert(0, current.getName() + " > ");
            current = current.getParent();
        }

        return address.toString();
    }
}