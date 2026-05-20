package com.taxcredit.adapter.persistence;

import com.taxcredit.domain.model.TaxCredit;
import com.taxcredit.domain.port.TaxCreditRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter that implements domain port using JPA repository
 * Hexagonal Architecture - Adapter pattern
 */
@Component
public class TaxCreditRepositoryAdapter implements TaxCreditRepository {
    
    private final JpaTaxCreditRepository jpaRepository;
    
    public TaxCreditRepositoryAdapter(JpaTaxCreditRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public TaxCredit save(TaxCredit taxCredit) {
        return jpaRepository.save(taxCredit);
    }
    
    @Override
    public Optional<TaxCredit> findById(Long id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    public Optional<TaxCredit> findByCitizenId(String citizenId) {
        return jpaRepository.findByCitizenId(citizenId);
    }
    
    @Override
    public boolean existsByCitizenId(String citizenId) {
        return jpaRepository.existsByCitizenId(citizenId);
    }
}

// Made with Bob
