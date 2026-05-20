package be.innallocation.adapter.out.persistence;

import be.innallocation.adapter.out.persistence.entity.DebtJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for DebtJpaEntity.
 */
@Repository
public interface SpringDataDebtRepository extends JpaRepository<DebtJpaEntity, UUID> {

    Optional<DebtJpaEntity> findByDebtId(String debtId);

    Optional<DebtJpaEntity> findByStructuredReference(String structuredReference);

    @Query("SELECT d FROM DebtJpaEntity d WHERE d.citizenId = :citizenId AND d.status IN ('ACTIVE', 'PARTIALLY_PAID') ORDER BY d.createdAt ASC")
    List<DebtJpaEntity> findEligibleForAllocationByCitizenId(String citizenId);

    @Query("SELECT d FROM DebtJpaEntity d ORDER BY d.createdAt DESC")
    List<DebtJpaEntity> findAllOrderByCreatedAtDesc();

    boolean existsByDebtId(String debtId);

    boolean existsByStructuredReference(String structuredReference);
}

// Made with Bob
