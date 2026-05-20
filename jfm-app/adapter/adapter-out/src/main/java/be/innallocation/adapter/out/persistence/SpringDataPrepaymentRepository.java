package be.innallocation.adapter.out.persistence;

import be.innallocation.adapter.out.persistence.entity.PrepaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Prepayment entities.
 */
@Repository
public interface SpringDataPrepaymentRepository extends JpaRepository<PrepaymentJpaEntity, UUID> {

    /**
     * Find prepayment by business identifier.
     *
     * @param prepaymentId the prepayment business ID
     * @return optional prepayment entity
     */
    Optional<PrepaymentJpaEntity> findByPrepaymentId(String prepaymentId);

    /**
     * Find prepayment by idempotency key.
     *
     * @param idempotencyKey the idempotency key
     * @return optional prepayment entity
     */
    Optional<PrepaymentJpaEntity> findByIdempotencyKey(String idempotencyKey);

    /**
     * Check if prepayment exists by business identifier.
     *
     * @param prepaymentId the prepayment business ID
     * @return true if exists
     */
    boolean existsByPrepaymentId(String prepaymentId);

    /**
     * Check if prepayment exists by idempotency key.
     *
     * @param idempotencyKey the idempotency key
     * @return true if exists
     */
    boolean existsByIdempotencyKey(String idempotencyKey);
}

// Made with Bob
