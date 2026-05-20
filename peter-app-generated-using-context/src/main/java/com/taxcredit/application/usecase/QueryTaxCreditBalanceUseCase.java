package com.taxcredit.application.usecase;

import com.taxcredit.domain.model.TaxCredit;
import com.taxcredit.domain.port.TaxCreditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Query Tax Credit Balance
 * FR-004: Query tax credit balances
 * RG-010: Citizen may access only their own data
 */
@Service
public class QueryTaxCreditBalanceUseCase {
    
    private final TaxCreditRepository taxCreditRepository;
    
    public QueryTaxCreditBalanceUseCase(TaxCreditRepository taxCreditRepository) {
        this.taxCreditRepository = taxCreditRepository;
    }
    
    @Transactional(readOnly = true)
    public TaxCredit execute(String citizenId) {
        return taxCreditRepository.findByCitizenId(citizenId)
            .orElseThrow(() -> new IllegalArgumentException("Tax credit not found for citizen: " + citizenId));
    }
}

// Made with Bob
