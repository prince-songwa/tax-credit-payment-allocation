package com.taxcredit.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AccountingEntry Entity
 * FR-013: Generate balanced accounting entries for prepayments, bank transfers, and allocations
 * RG-006: Total debit equals total credit (double-entry bookkeeping)
 */
@Entity
@Table(name = "accounting_entries")
@Getter
@NoArgsConstructor
public class AccountingEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String transactionId;
    
    @Column(nullable = false)
    private String accountCode;
    
    @Column(nullable = false)
    private String accountName;
    
    @Column(nullable = false)
    private String entryType; // DEBIT or CREDIT
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency = Currency.EUR;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private String referenceType; // PREPAYMENT, PAYMENT, ALLOCATION
    
    @Column(nullable = false)
    private Long referenceId;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public AccountingEntry(String transactionId, String accountCode, String accountName,
                          String entryType, BigDecimal amount, String description,
                          String referenceType, Long referenceId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Accounting entry amount must be positive");
        }
        if (!entryType.equals("DEBIT") && !entryType.equals("CREDIT")) {
            throw new IllegalArgumentException("Entry type must be DEBIT or CREDIT");
        }
        this.transactionId = transactionId;
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.entryType = entryType;
        this.amount = amount;
        this.currency = Currency.EUR;
        this.description = description;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.createdAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    /**
     * Check if entry is a debit
     */
    public boolean isDebit() {
        return "DEBIT".equals(this.entryType);
    }
    
    /**
     * Check if entry is a credit
     */
    public boolean isCredit() {
        return "CREDIT".equals(this.entryType);
    }
}

// Made with Bob
