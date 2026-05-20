package com.taxcredit.domain.port;

import com.taxcredit.domain.model.Debt;
import com.taxcredit.domain.model.DebtStatus;

import java.util.List;
import java.util.Optional;

/**
 * Port interface for Debt repository
 */
public interface DebtRepository {
    
    Debt save(Debt debt);
    
    Optional<Debt> findById(Long id);
    
    Optional<Debt> findByDebtReference(String debtReference);
    
    List<Debt> findByCitizenId(String citizenId);
    
    List<Debt> findByCitizenIdAndStatus(String citizenId, DebtStatus status);
    
    List<Debt> findOutstandingDebtsByCitizenIdOrderByDueDateAsc(String citizenId);
}

// Made with Bob
