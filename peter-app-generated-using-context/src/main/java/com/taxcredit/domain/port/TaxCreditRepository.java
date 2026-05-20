package com.taxcredit.domain.port;

import com.taxcredit.domain.model.TaxCredit;

import java.util.Optional;

/**
 * Port interface for TaxCredit repository
 * Hexagonal Architecture - Domain layer defines the interface
 */
public interface TaxCreditRepository {
    
    TaxCredit save(TaxCredit taxCredit);
    
    Optional<TaxCredit> findById(Long id);
    
    Optional<TaxCredit> findByCitizenId(String citizenId);
    
    boolean existsByCitizenId(String citizenId);
}

// Made with Bob
