package auca.ac.rw.carRentalHub.model;

import java.util.UUID;
import java.time.LocalDate;
import java.math.BigDecimal;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import auca.ac.rw.carRentalHub.model.enums.EReservationStatus;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String reservationNumber;

    private LocalDate pickupDate;
    private LocalDate returnDate;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private EReservationStatus status;

    /**
     * MANY-TO-ONE RELATIONSHIP with Customer
     * 
     * EXPLANATION:
     * - Many reservations belong to one customer
     * - @JoinColumn defines the foreign key column name in the reservation table
     * - This is the OWNING side of the relationship (contains the foreign key)
     * - The foreign key "customer_id" references the customer table
     */
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /**
     * MANY-TO-ONE RELATIONSHIP with Vehicle
     */
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    /**
     * ONE-TO-ONE RELATIONSHIP with Payment
     * mappedBy indicates this is the inverse side
     */
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    @JsonIgnore
    private Payment payment;

    // Constructors
    public Reservation() {}

    public Reservation(String reservationNumber, LocalDate pickupDate, LocalDate returnDate) {
        this.reservationNumber = reservationNumber;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.status = EReservationStatus.PENDING;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { 
        this.reservationNumber = reservationNumber; 
    }

    public LocalDate getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDate pickupDate) { this.pickupDate = pickupDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public EReservationStatus getStatus() { return status; }
    public void setStatus(EReservationStatus status) { this.status = status; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
}