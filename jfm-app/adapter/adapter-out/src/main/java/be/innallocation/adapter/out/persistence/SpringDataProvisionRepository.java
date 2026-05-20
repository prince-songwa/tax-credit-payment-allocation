package be.innallocation.adapter.out.persistence;

import be.innallocation.adapter.out.persistence.entity.ProvisionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for ProvisionJpaEntity.
 */
@Repository
public interface SpringDataProvisionRepository extends JpaRepository<ProvisionJpaEntity, UUID> {

    Optional<ProvisionJpaEntity> findByProvisionId(String provisionId);

    Optional<ProvisionJpaEntity> findByCitizenId(String citizenId);

    boolean existsByCitizenId(String citizenId);
}

// Made with Bob
