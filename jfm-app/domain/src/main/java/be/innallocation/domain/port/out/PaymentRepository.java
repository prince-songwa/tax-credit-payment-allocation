package be.innallocation.domain.port.out;

import be.innallocation.domain.model.Payment;

import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for Payment persistence.
 */
public interface PaymentRepository {

    /**
     * Save a payment.
     * 
     * @param payment Payment to save
     * @return Saved payment
     */
    Payment save(Payment payment);

    /**
     * Find a payment by its technical ID.
     * 
     * @param id Technical ID
     * @return Optional containing the payment if found
     */
    Optional<Payment> findById(UUID id);

    /**
     * Find a payment by its business ID.
     * 
     * @param paymentId Business identifier
     * @return Optional containing the payment if found
     */
    Optional<Payment> findByPaymentId(String paymentId);

    /**
     * Find a payment by its bank reference.
     * Bank reference is UNIQUE to prevent duplicate processing.
     * 
     * @param bankReference Bank reference
     * @return Optional containing the payment if found
     */
    Optional<Payment> findByBankReference(String bankReference);

    /**
     * Find all payments ordered by creation date (newest first).
     * Used for listing/display purposes.
     *
     * @return List of all payments ordered by creation date descending
     */
    java.util.List<Payment> findAll();

    /**
     * Check if a payment exists by its business ID.
     *
     * @param paymentId Business identifier
     * @return true if exists
     */
    boolean existsByPaymentId(String paymentId);

    /**
     * Check if a payment exists by its bank reference.
     * Used to prevent duplicate payment processing.
     * 
     * @param bankReference Bank reference
     * @return true if exists
     */
    boolean existsByBankReference(String bankReference);
}

// Made with Bob
