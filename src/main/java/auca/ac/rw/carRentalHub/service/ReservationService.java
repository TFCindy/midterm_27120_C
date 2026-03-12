package auca.ac.rw.carRentalHub.service;

import java.util.UUID;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import auca.ac.rw.carRentalHub.model.Reservation;
import auca.ac.rw.carRentalHub.model.Customer;
import auca.ac.rw.carRentalHub.model.Vehicle;
import auca.ac.rw.carRentalHub.model.Payment;
import auca.ac.rw.carRentalHub.repository.ReservationRepository;
import auca.ac.rw.carRentalHub.repository.CustomerRepository;
import auca.ac.rw.carRentalHub.repository.VehicleRepository;
import auca.ac.rw.carRentalHub.repository.PaymentRepository;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public Reservation saveReservation(Reservation reservation, UUID customerId, UUID vehicleId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        reservation.setCustomer(customer);
        reservation.setVehicle(vehicle);
        return reservationRepository.save(reservation);
    }

    public Optional<Reservation> getReservation(UUID id) {
        return reservationRepository.findById(id);
    }

    public Page<Reservation> listReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable);
    }

    public Page<Reservation> listByCustomer(UUID customerId, Pageable pageable) {
        return reservationRepository.findReservationsByCustomerId(customerId, pageable);
    }

    @Transactional
    public Payment addPayment(UUID reservationId, Payment payment) {
        Optional<Reservation> r = reservationRepository.findById(reservationId);
        if (r.isEmpty()) {
            throw new IllegalArgumentException("Reservation not found");
        }
        Reservation reservation = r.get();
        payment.setReservation(reservation);
        reservation.setPayment(payment);
        Payment savedPayment = paymentRepository.save(payment);
        reservationRepository.save(reservation);
        return savedPayment;
    }
}
