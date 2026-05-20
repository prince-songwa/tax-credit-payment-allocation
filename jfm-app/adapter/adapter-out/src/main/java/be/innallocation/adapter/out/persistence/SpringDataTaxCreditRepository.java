package be.innallocation.adapter.out.persistence;

import be.innallocation.adapter.out.persistence.entity.TaxCreditJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for TaxCredit entities.
 */
@Repository
public interface SpringDataTaxCreditRepository extends JpaRepository<TaxCreditJpaEntity, UUID> {

    /**
     * Find tax credit by business identifier.
     *
     * @param taxCreditId the tax credit business ID
     * @return optional tax credit entity
     */
    Optional<TaxCreditJpaEntity> findByTaxCreditId(String taxCreditId);

    /**
     * Find all tax credits ordered by creation date descending.
     *
     * @return list of all tax credits
     */
    @Query("SELECT tc FROM TaxCreditJpaEntity tc ORDER BY tc.createdAt DESC")
    List<TaxCreditJpaEntity> findAllOrderByCreatedAtDesc();

    /**
     * Check if tax credit exists by business identifier.
     *
     * @param taxCreditId the tax credit business ID
     * @return true if exists
     */
    boolean existsByTaxCreditId(String taxCreditId);
}

// Made with Bob
