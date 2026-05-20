package com.taxcredit.domain.port;

import com.taxcredit.domain.model.Payment;

import java.util.List;
import java.util.Optional;

/**
 * Port interface for Payment repository
 */
public interface PaymentRepository {
    
    Payment save(Payment payment);
    
    Optional<Payment> findById(Long id);
    
    Optional<Payment> findByBankReference(String bankReference);
    
    List<Payment> findByDebtReference(String debtReference);
    
    boolean existsByBankReference(String bankReference);
}

// Made with Bob
