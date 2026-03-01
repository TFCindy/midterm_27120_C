package auca.ac.rw.carRentalHub.model;

import java.util.UUID;

import auca.ac.rw.carRentalHub.model.enums.EPaymentStatus;
import auca.ac.rw.carRentalHub.model.enums.EPaymentMethod;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String paymentReference;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private EPaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private EPaymentStatus paymentStatus;

    private LocalDateTime transactionDate;

    /**
     * ONE-TO-ONE RELATIONSHIP with Reservation
     * 
     * EXPLANATION:
     * - One payment belongs to exactly one reservation
     * - This is the OWNING side of the relationship (contains the foreign key)
     * - @JoinColumn with unique=true ensures one-to-one constraint
     * - The foreign key "reservation_id" references the reservation table
     * - The unique constraint prevents multiple payments for same reservation
     */
    @OneToOne
    @JoinColumn(name = "reservation_id", unique = true)
    private Reservation reservation;

    // Constructors
    public Payment() {}

    public Payment(String paymentReference, BigDecimal amount, EPaymentMethod paymentMethod) {
        this.paymentReference = paymentReference;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = EPaymentStatus.PENDING;
        this.transactionDate = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { 
        this.paymentReference = paymentReference; 
    }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public EPaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(EPaymentMethod paymentMethod) { 
        this.paymentMethod = paymentMethod; 
    }

    public EPaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(EPaymentStatus paymentStatus) { 
        this.paymentStatus = paymentStatus; 
    }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { 
        this.transactionDate = transactionDate; 
    }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { 
        this.reservation = reservation; 
    }
}