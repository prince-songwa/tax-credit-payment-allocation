package com.taxcredit.adapter.persistence;

import com.taxcredit.domain.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository interfaces for all entities
 */

@Repository
interface JpaPrepaymentRepository extends JpaRepository<Prepayment, Long> {
    Optional<Prepayment> findByIdempotencyKey(String idempotencyKey);
    List<Prepayment> findByCitizenId(String citizenId);
    boolean existsByIdempotencyKey(String idempotencyKey);
}

@Repository
interface JpaDebtRepository extends JpaRepository<Debt, Long> {
    Optional<Debt> findByDebtReference(String debtReference);
    List<Debt> findByCitizenId(String citizenId);
    List<Debt> findByCitizenIdAndStatus(String citizenId, DebtStatus status);
    
    @Query("SELECT d FROM Debt d WHERE d.citizenId = :citizenId AND d.status IN ('OUTSTANDING', 'PARTIALLY_PAID') ORDER BY d.dueDate ASC, d.createdAt ASC")
    List<Debt> findOutstandingDebtsByCitizenIdOrderByDueDateAsc(String citizenId);
}

@Repository
interface JpaPaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBankReference(String bankReference);
    List<Payment> findByDebtReference(String debtReference);
    boolean existsByBankReference(String bankReference);
}

@Repository
interface JpaAllocationRepository extends JpaRepository<Allocation, Long> {
    List<Allocation> findByDebtId(Long debtId);
    List<Allocation> findByCitizenId(String citizenId);
}

@Repository
interface JpaAccountingEntryRepository extends JpaRepository<AccountingEntry, Long> {
    List<AccountingEntry> findByTransactionId(String transactionId);
    List<AccountingEntry> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
}

// Made with Bob
