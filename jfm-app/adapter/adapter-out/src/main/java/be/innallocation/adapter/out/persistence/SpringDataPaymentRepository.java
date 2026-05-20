package be.innallocation.adapter.out.persistence;

import be.innallocation.adapter.out.persistence.entity.PaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Payment entities.
 */
@Repository
public interface SpringDataPaymentRepository extends JpaRepository<PaymentJpaEntity, UUID> {

    /**
     * Find payment by business identifier.
     *
     * @param paymentId the payment business ID
     * @return optional payment entity
     */
    Optional<PaymentJpaEntity> findByPaymentId(String paymentId);

    /**
     * Find payment by bank reference.
     *
     * @param bankReference the bank reference
     * @return optional payment entity
     */
    Optional<PaymentJpaEntity> findByBankReference(String bankReference);

    /**
     * Find payment by structured reference.
     *
     * @param structuredReference the structured reference
     * @return optional payment entity
     */
    Optional<PaymentJpaEntity> findByStructuredReference(String structuredReference);

    /**
     * Find all payments ordered by creation date descending.
     *
     * @return list of all payments
     */
    @Query("SELECT p FROM PaymentJpaEntity p ORDER BY p.createdAt DESC")
    List<PaymentJpaEntity> findAllOrderByCreatedAtDesc();

    /**
     * Check if payment exists by business identifier.
     *
     * @param paymentId the payment business ID
     * @return true if exists
     */
    boolean existsByPaymentId(String paymentId);

    /**
     * Check if payment exists by bank reference.
     *
     * @param bankReference the bank reference
     * @return true if exists
     */
    boolean existsByBankReference(String bankReference);
}

// Made with Bob
