package auca.ac.rw.carRentalHub.repository;

import java.util.UUID;
import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import auca.ac.rw.carRentalHub.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    /**
     * More existsBy() examples
     */
    
    // Check if license number already exists
    boolean existsByLicenseNumber(String licenseNumber);
    
    // Check if email already exists
    boolean existsByEmail(String email);
    
    // Check if customer has valid license (not expired)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
           "FROM Customer c WHERE c.id = :customerId " +
           "AND c.licenseExpiryDate > CURRENT_DATE")
    boolean hasValidLicense(@Param("customerId") UUID customerId);
    
    // Check if customer has active reservations
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
           "FROM Customer c JOIN c.reservations r " +
           "WHERE c.id = :customerId AND r.status = 'ACTIVE'")
    boolean hasActiveReservations(@Param("customerId") UUID customerId);
    
    // Find customers with expiring licenses
    List<Customer> findByLicenseExpiryDateBetween(LocalDate start, LocalDate end);
}