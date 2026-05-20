package be.innallocation.adapter.out.persistence;

import be.innallocation.adapter.out.persistence.entity.AllocationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for AllocationJpaEntity.
 */
@Repository
public interface SpringDataAllocationRepository extends JpaRepository<AllocationJpaEntity, UUID> {

    Optional<AllocationJpaEntity> findByAllocationId(String allocationId);

    List<AllocationJpaEntity> findByDebtId(UUID debtId);

    List<AllocationJpaEntity> findBySourceId(UUID sourceId);

    boolean existsByAllocationId(String allocationId);
}

// Made with Bob