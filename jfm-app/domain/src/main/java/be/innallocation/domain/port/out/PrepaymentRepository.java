package be.innallocation.domain.port.out;

import be.innallocation.domain.model.Prepayment;

import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for Prepayment persistence.
 */
public interface PrepaymentRepository {

    /**
     * Save a prepayment.
     * 
     * @param prepayment Prepayment to save
     * @return Saved prepayment
     */
    Prepayment save(Prepayment prepayment);

    /**
     * Find a prepayment by its technical ID.
     * 
     * @param id Technical ID
     * @return Optional containing the prepayment if found
     */
    Optional<Prepayment> findById(UUID id);

    /**
     * Find a prepayment by its business ID.
     * 
     * @param prepaymentId Business identifier
     * @return Optional containing the prepayment if found
     */
    Optional<Prepayment> findByPrepaymentId(String prepaymentId);

    /**
     * Find a prepayment by its idempotency key.
     * Used to prevent duplicate processing (RG-003).
     * 
     * @param idempotencyKey Idempotency key
     * @return Optional containing the prepayment if found
     */
    Optional<Prepayment> findByIdempotencyKey(String idempotencyKey);

    /**
     * Check if a prepayment exists by its business ID.
     * 
     * @param prepaymentId Business identifier
     * @return true if exists
     */
    boolean existsByPrepaymentId(String prepaymentId);

    /**
     * Check if a prepayment exists by its idempotency key.
     * 
     * @param idempotencyKey Idempotency key
     * @return true if exists
     */
    boolean existsByIdempotencyKey(String idempotencyKey);
}

// Made with Bob
