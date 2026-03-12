package auca.ac.rw.carRentalHub.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import auca.ac.rw.carRentalHub.model.Location;
import auca.ac.rw.carRentalHub.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

       /**
        * existsBy() METHODS DEMONSTRATION
        * 
        * EXPLANATION:
        * Spring Data JPA automatically implements these methods based on method naming
        * conventions.
        * The framework parses the method name, creates the appropriate JPQL query,
        * and returns true if at least one result exists.
        * 
        * How it works:
        * 1. Method name starts with "existsBy"
        * 2. Followed by property names (Username, Email, etc.)
        * 3. Spring generates: SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
        * FROM User u WHERE u.username = ?1
        * 4. More efficient than using .findBy().isPresent() because it doesn't fetch
        * the entity
        */

       // Basic existence checks
       boolean existsByUsername(String username);

       boolean existsByEmail(String email);

       // Check with multiple conditions (AND)
       boolean existsByUsernameAndEmail(String username, String email);

       // Check for existence excluding a specific ID (useful for updates)
       @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u " +
                     "WHERE u.email = :email AND u.id != :id")
       boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") UUID id);

       // Complex existence check with location and role name
       // Use nested property to check role name
       boolean existsByLocationAndRole_Name(Location location, String roleName);

       // Find users by a collection of location IDs (used with hierarchy traversal)
       List<User> findByLocationIdIn(List<UUID> locationIds);
}