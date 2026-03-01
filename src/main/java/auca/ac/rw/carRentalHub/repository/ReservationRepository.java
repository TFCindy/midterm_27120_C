package auca.ac.rw.carRentalHub.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import auca.ac.rw.carRentalHub.model.Reservation;
import auca.ac.rw.carRentalHub.model.Customer;
import auca.ac.rw.carRentalHub.model.Vehicle;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    
    // Find by customer
    List<Reservation> findByCustomer(Customer customer);
    
    // Find by vehicle
    List<Reservation> findByVehicle(Vehicle vehicle);
    
    // Find by status with pagination
    Page<Reservation> findByStatus(String status, Pageable pageable);
    
    // Check if exists (for requirement #7)
    boolean existsByReservationNumber(String reservationNumber);
    
    // Custom query with pagination
    @Query("SELECT r FROM Reservation r WHERE r.customer.id = :customerId")
    Page<Reservation> findReservationsByCustomerId(@Param("customerId") UUID customerId, Pageable pageable);
    
    // Count active reservations for a vehicle
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.vehicle.id = :vehicleId AND r.status = 'ACTIVE'")
    long countActiveReservationsForVehicle(@Param("vehicleId") UUID vehicleId);
}