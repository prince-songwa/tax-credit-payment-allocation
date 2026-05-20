package com.taxcredit.domain.port;

import com.taxcredit.domain.model.Prepayment;

import java.util.List;
import java.util.Optional;

/**
 * Port interface for Prepayment repository
 */
public interface PrepaymentRepository {
    
    Prepayment save(Prepayment prepayment);
    
    Optional<Prepayment> findById(Long id);
    
    Optional<Prepayment> findByIdempotencyKey(String idempotencyKey);
    
    List<Prepayment> findByCitizenId(String citizenId);
    
    boolean existsByIdempotencyKey(String idempotencyKey);
}

// Made with Bob
