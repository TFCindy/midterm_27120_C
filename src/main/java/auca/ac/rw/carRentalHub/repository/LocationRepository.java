package auca.ac.rw.carRentalHub.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import auca.ac.rw.carRentalHub.model.Location;
import auca.ac.rw.carRentalHub.model.enums.ELocationType;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    
    // Check if location code exists
    boolean existsByCode(String code);
    
    // Find by type (PROVINCE, DISTRICT, etc.)
    List<Location> findByType(ELocationType type);
    
    // Find by parent
    List<Location> findByParent(Location parent);
    
    // Find all provinces (type = PROVINCE)
    List<Location> findByTypeOrderByNameAsc(ELocationType type);
    
    // Find by name containing (for search)
    List<Location> findByNameContainingIgnoreCase(String name);
    
    // Custom query to find all descendants of a location
    @Query("SELECT l FROM Location l WHERE l.parent.id = :parentId")
    List<Location> findChildrenByParentId(@Param("parentId") UUID parentId);
    
    // Find location by code or name (for requirement #8)
    @Query("SELECT l FROM Location l WHERE l.code = :identifier OR l.name = :identifier")
    Location findByIdentifier(@Param("identifier") String identifier);

    @Query(value = """
            WITH RECURSIVE location_tree AS (
                SELECT id, name, type, parent_id FROM location WHERE id = :parentId
                UNION ALL
                SELECT l.id, l.name, l.type, l.parent_id FROM location l
                INNER JOIN location_tree lt ON l.parent_id = lt.id
            )
            SELECT id FROM location_tree
            """, nativeQuery = true)
    List<UUID> findAllDescendantIds(@Param("parentId") UUID parentId);
}