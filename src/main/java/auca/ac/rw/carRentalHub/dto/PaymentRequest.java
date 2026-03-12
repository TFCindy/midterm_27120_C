package auca.ac.rw.carRentalHub.dto;

import java.math.BigDecimal;
import java.util.UUID;

import auca.ac.rw.carRentalHub.model.enums.EPaymentMethod;

public class PaymentRequest {

    private String paymentReference;
    private BigDecimal amount;
    private EPaymentMethod paymentMethod;
    private UUID reservationId;

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

    public UUID getReservationId() {
        return reservationId;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }
}

