package auca.ac.rw.carRentalHub.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import auca.ac.rw.carRentalHub.dto.UserCreateRequest;
import auca.ac.rw.carRentalHub.dto.UserDTO;
import auca.ac.rw.carRentalHub.model.Location;
import auca.ac.rw.carRentalHub.model.Role;
import auca.ac.rw.carRentalHub.model.User;
import auca.ac.rw.carRentalHub.model.enums.ELocationType;
import auca.ac.rw.carRentalHub.repository.LocationRepository;
import auca.ac.rw.carRentalHub.repository.RoleRepository;
import auca.ac.rw.carRentalHub.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Get paged users (for pagination demonstration)
     */
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * REQUIREMENT #8: Get all users from a province
     * 
     * @param identifier - province code OR province name
     * @return List of UserDTOs
     */
    public List<UserDTO> getUsersByProvince(String identifier) {
        Location province = locationRepository.findByIdentifier(identifier);
        if (province == null) {
            throw new IllegalArgumentException("Province not found with identifier: " + identifier);
        }

        if (province.getType() != ELocationType.PROVINCE) {
            throw new IllegalArgumentException("Location '" + identifier + "' is not a province");
        }

        List<UUID> locationIds = new ArrayList<>();
        locationIds.add(province.getId());
        locationIds.addAll(locationRepository.findAllDescendantIds(province.getId()));

        List<User> users = userRepository.findByLocationIdIn(locationIds);

        // Convert to DTOs (hide sensitive data)
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Simple existsBy demonstration used by controller
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Alternative implementation with location validation
     */
    public List<UserDTO> getUsersByProvinceWithValidation(String identifier) {
        // First, verify that the identifier corresponds to a province
        Location province = locationRepository.findByIdentifier(identifier);

        if (province == null) {
            throw new RuntimeException("Province not found with identifier: " + identifier);
        }

        // Check if it's actually a province
        if (province.getType() != ELocationType.PROVINCE) {
            throw new RuntimeException("Location '" + identifier + "' is not a province");
        }

        // Get users
        List<UUID> locationIds = new ArrayList<>();
        locationIds.add(province.getId());
        locationIds.addAll(locationRepository.findAllDescendantIds(province.getId()));

        List<User> users = userRepository.findByLocationIdIn(locationIds);

        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getUsersByLocation(String identifier) {
        Location location = locationRepository.findByIdentifier(identifier);
        if (location == null) {
            throw new IllegalArgumentException("Location not found with identifier: " + identifier);
        }

        List<UUID> locationIds = new ArrayList<>();
        locationIds.add(location.getId());
        locationIds.addAll(locationRepository.findAllDescendantIds(location.getId()));

        List<User> users = userRepository.findByLocationIdIn(locationIds);

        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a user using only village name or code.
     * This enforces that users are attached to a VILLAGE and linked upwards via the Location hierarchy.
     */
    public UserDTO createUser(UserCreateRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getVillageIdentifier() == null || request.getVillageIdentifier().isBlank()) {
            throw new IllegalArgumentException("Village identifier is required");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Location village = locationRepository.findByIdentifier(request.getVillageIdentifier());
        if (village == null) {
            throw new IllegalArgumentException("Village not found with identifier: " + request.getVillageIdentifier());
        }

        if (village.getType() != ELocationType.VILLAGE) {
            throw new IllegalArgumentException("Users must be created using a VILLAGE identifier only");
        }

        Role role = null;
        if (request.getRoleName() != null && !request.getRoleName().isBlank()) {
            role = roleRepository.findByName(request.getRoleName())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + request.getRoleName()));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(role);
        user.setLocation(village);

        User saved = userRepository.save(user);
        return convertToDTO(saved);
    }

    /**
     * Convert User entity to UserDTO (hides sensitive data)
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getName());
        }

        if (user.getLocation() != null) {
            dto.setLocationName(user.getLocation().getName());
            dto.setLocationType(user.getLocation().getType());

            // Add province name for clarity
            Location loc = user.getLocation();
            while (loc.getParent() != null) {
                loc = loc.getParent();
            }
            dto.setProvinceName(loc.getName());
        }

        return dto;
    }
}