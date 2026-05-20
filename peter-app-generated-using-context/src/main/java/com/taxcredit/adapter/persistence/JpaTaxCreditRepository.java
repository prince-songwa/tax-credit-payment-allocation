package com.taxcredit.adapter.persistence;

import com.taxcredit.domain.model.TaxCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository Adapter for TaxCredit
 * Hexagonal Architecture - Infrastructure layer implements domain port
 */
@Repository
public interface JpaTaxCreditRepository extends JpaRepository<TaxCredit, Long> {
    
    Optional<TaxCredit> findByCitizenId(String citizenId);
    
    boolean existsByCitizenId(String citizenId);
}

// Made with Bob
