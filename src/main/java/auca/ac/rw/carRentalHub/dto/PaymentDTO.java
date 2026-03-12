package auca.ac.rw.carRentalHub.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import auca.ac.rw.carRentalHub.model.enums.EPaymentMethod;
import auca.ac.rw.carRentalHub.model.enums.EPaymentStatus;

public class PaymentDTO {

    private UUID id;
    private String paymentReference;
    private BigDecimal amount;
    private EPaymentMethod paymentMethod;
    private EPaymentStatus paymentStatus;
    private LocalDateTime transactionDate;
    private UUID reservationId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public EPaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(EPaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public EPaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(EPaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }
}

