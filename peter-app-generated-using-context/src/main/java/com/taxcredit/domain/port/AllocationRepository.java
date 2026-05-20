package com.taxcredit.domain.port;

import com.taxcredit.domain.model.Allocation;

import java.util.List;
import java.util.Optional;

/**
 * Port interface for Allocation repository
 */
public interface AllocationRepository {
    
    Allocation save(Allocation allocation);
    
    Optional<Allocation> findById(Long id);
    
    List<Allocation> findByDebtId(Long debtId);
    
    List<Allocation> findByCitizenId(String citizenId);
}

// Made with Bob
