package com.taxcredit.adapter.persistence;

import com.taxcredit.domain.model.*;
import com.taxcredit.domain.port.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapters that implement domain ports using JPA repositories
 */

@Component
class PrepaymentRepositoryAdapter implements PrepaymentRepository {
    private final JpaPrepaymentRepository jpaRepository;
    
    PrepaymentRepositoryAdapter(JpaPrepaymentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Prepayment save(Prepayment prepayment) {
        return jpaRepository.save(prepayment);
    }
    
    @Override
    public Optional<Prepayment> findById(Long id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    public Optional<Prepayment> findByIdempotencyKey(String idempotencyKey) {
        return jpaRepository.findByIdempotencyKey(idempotencyKey);
    }
    
    @Override
    public List<Prepayment> findByCitizenId(String citizenId) {
        return jpaRepository.findByCitizenId(citizenId);
    }
    
    @Override
    public boolean existsByIdempotencyKey(String idempotencyKey) {
        return jpaRepository.existsByIdempotencyKey(idempotencyKey);
    }
}

@Component
class DebtRepositoryAdapter implements DebtRepository {
    private final JpaDebtRepository jpaRepository;
    
    DebtRepositoryAdapter(JpaDebtRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Debt save(Debt debt) {
        return jpaRepository.save(debt);
    }
    
    @Override
    public Optional<Debt> findById(Long id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    public Optional<Debt> findByDebtReference(String debtReference) {
        return jpaRepository.findByDebtReference(debtReference);
    }
    
    @Override
    public List<Debt> findByCitizenId(String citizenId) {
        return jpaRepository.findByCitizenId(citizenId);
    }
    
    @Override
    public List<Debt> findByCitizenIdAndStatus(String citizenId, DebtStatus status) {
        return jpaRepository.findByCitizenIdAndStatus(citizenId, status);
    }
    
    @Override
    public List<Debt> findOutstandingDebtsByCitizenIdOrderByDueDateAsc(String citizenId) {
        return jpaRepository.findOutstandingDebtsByCitizenIdOrderByDueDateAsc(citizenId);
    }
}

@Component
class PaymentRepositoryAdapter implements PaymentRepository {
    private final JpaPaymentRepository jpaRepository;
    
    PaymentRepositoryAdapter(JpaPaymentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Payment save(Payment payment) {
        return jpaRepository.save(payment);
    }
    
    @Override
    public Optional<Payment> findById(Long id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    public Optional<Payment> findByBankReference(String bankReference) {
        return jpaRepository.findByBankReference(bankReference);
    }
    
    @Override
    public List<Payment> findByDebtReference(String debtReference) {
        return jpaRepository.findByDebtReference(debtReference);
    }
    
    @Override
    public boolean existsByBankReference(String bankReference) {
        return jpaRepository.existsByBankReference(bankReference);
    }
}

@Component
class AllocationRepositoryAdapter implements AllocationRepository {
    private final JpaAllocationRepository jpaRepository;
    
    AllocationRepositoryAdapter(JpaAllocationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Allocation save(Allocation allocation) {
        return jpaRepository.save(allocation);
    }
    
    @Override
    public Optional<Allocation> findById(Long id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    public List<Allocation> findByDebtId(Long debtId) {
        return jpaRepository.findByDebtId(debtId);
    }
    
    @Override
    public List<Allocation> findByCitizenId(String citizenId) {
        return jpaRepository.findByCitizenId(citizenId);
    }
}

@Component
class AccountingEntryRepositoryAdapter implements AccountingEntryRepository {
    private final JpaAccountingEntryRepository jpaRepository;
    
    AccountingEntryRepositoryAdapter(JpaAccountingEntryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public AccountingEntry save(AccountingEntry accountingEntry) {
        return jpaRepository.save(accountingEntry);
    }
    
    @Override
    public List<AccountingEntry> saveAll(List<AccountingEntry> entries) {
        return jpaRepository.saveAll(entries);
    }
    
    @Override
    public List<AccountingEntry> findByTransactionId(String transactionId) {
        return jpaRepository.findByTransactionId(transactionId);
    }
    
    @Override
    public List<AccountingEntry> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId) {
        return jpaRepository.findByReferenceTypeAndReferenceId(referenceType, referenceId);
    }
}

// Made with Bob
