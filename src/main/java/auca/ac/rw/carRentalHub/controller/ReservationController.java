package auca.ac.rw.carRentalHub.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import auca.ac.rw.carRentalHub.dto.PaymentDTO;
import auca.ac.rw.carRentalHub.dto.PaymentRequest;
import auca.ac.rw.carRentalHub.dto.ReservationDTO;
import auca.ac.rw.carRentalHub.dto.ReservationRequest;
import auca.ac.rw.carRentalHub.model.Payment;
import auca.ac.rw.carRentalHub.model.Reservation;
import auca.ac.rw.carRentalHub.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservationDTO> saveReservation(@RequestBody ReservationRequest request) {
        Reservation reservation = new Reservation();
        reservation.setReservationNumber(request.getReservationNumber());
        reservation.setPickupDate(request.getPickupDate());
        reservation.setReturnDate(request.getReturnDate());
        reservation.setTotalAmount(request.getTotalAmount());

        Reservation saved = reservationService.saveReservation(reservation, request.getCustomerId(),
                request.getVehicleId());
        return ResponseEntity.status(201).body(toDto(saved));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservationDTO> getReservation(@PathVariable UUID id) {
        return reservationService.getReservation(id)
                .map(r -> ResponseEntity.ok(toDto(r)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> listReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "pickupDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Reservation> result = reservationService.listReservations(pageable);
        List<ReservationDTO> content = result.map(this::toDto).getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("currentPage", result.getNumber());
        response.put("totalPages", result.getTotalPages());
        response.put("totalItems", result.getTotalElements());
        response.put("pageSize", result.getSize());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/customer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> reservationsByCustomer(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> result = reservationService.listByCustomer(customerId, pageable);
        List<ReservationDTO> content = result.map(this::toDto).getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("currentPage", result.getNumber());
        response.put("totalPages", result.getTotalPages());
        response.put("totalItems", result.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{id}/payment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentDTO> addPayment(@PathVariable UUID id,
            @RequestBody PaymentRequest request) {
        Payment payment = new Payment();
        payment.setPaymentReference(request.getPaymentReference());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());

        Payment saved = reservationService.addPayment(id, payment);
        return ResponseEntity.status(201).body(toDto(saved));
    }

    private ReservationDTO toDto(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setReservationNumber(reservation.getReservationNumber());
        dto.setPickupDate(reservation.getPickupDate());
        dto.setReturnDate(reservation.getReturnDate());
        dto.setTotalAmount(reservation.getTotalAmount());
        dto.setStatus(reservation.getStatus());
        if (reservation.getCustomer() != null) {
            dto.setCustomerId(reservation.getCustomer().getId());
        }
        if (reservation.getVehicle() != null) {
            dto.setVehicleId(reservation.getVehicle().getId());
        }
        return dto;
    }

    private PaymentDTO toDto(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setPaymentReference(payment.getPaymentReference());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setTransactionDate(payment.getTransactionDate());
        if (payment.getReservation() != null) {
            dto.setReservationId(payment.getReservation().getId());
        }
        return dto;
    }
}

