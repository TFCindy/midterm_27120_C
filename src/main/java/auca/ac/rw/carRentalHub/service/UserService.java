package auca.ac.rw.carRentalHub.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import auca.ac.rw.carRentalHub.dto.UserDTO;
import auca.ac.rw.carRentalHub.model.User;
import auca.ac.rw.carRentalHub.repository.UserRepository;
import auca.ac.rw.carRentalHub.repository.LocationRepository;
import auca.ac.rw.carRentalHub.model.Location;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LocationRepository locationRepository;

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
        // Find all users in the province using our custom query
        List<User> users = userRepository.findUsersByProvinceIdentifier(identifier);
        
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
        if (!"PROVINCE".equals(province.getType())) {
            throw new RuntimeException("Location '" + identifier + "' is not a province");
        }
        
        // Get users
        List<User> users = userRepository.findUsersByProvinceIdentifier(identifier);
        
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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