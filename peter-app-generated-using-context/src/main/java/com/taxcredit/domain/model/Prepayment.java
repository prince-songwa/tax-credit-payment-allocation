package com.taxcredit.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Prepayment Entity
 * FR-002: Accept citizen prepayments (VAT prepayments and advance payments)
 * RG-001: Amount must be 0.01 - 999,999.99 EUR
 * RG-003: Unique idempotency key
 */
@Entity
@Table(name = "prepayments")
@Getter
@NoArgsConstructor
public class Prepayment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String paymentReference;
    
    @Column(nullable = false, unique = true)
    private String idempotencyKey;
    
    @Column(nullable = false)
    private String citizenId;
    
    @Column(nullable = false)
    private Long taxCreditId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrepaymentType type;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency = Currency.EUR;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrepaymentStatus status = PrepaymentStatus.PENDING;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    private LocalDateTime completedAt;
    
    public Prepayment(String paymentReference, String idempotencyKey, String citizenId, 
                      Long taxCreditId, PrepaymentType type, BigDecimal amount) {
        validateAmount(amount);
        this.paymentReference = paymentReference;
        this.idempotencyKey = idempotencyKey;
        this.citizenId = citizenId;
        this.taxCreditId = taxCreditId;
        this.type = type;
        this.amount = amount;
        this.currency = Currency.EUR;
        this.status = PrepaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Validate amount according to RG-001
     */
    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        BigDecimal minAmount = new BigDecimal("0.01");
        BigDecimal maxAmount = new BigDecimal("999999.99");
        
        if (amount.compareTo(minAmount) < 0 || amount.compareTo(maxAmount) > 0) {
            throw new IllegalArgumentException(
                "Amount must be between 0.01 and 999,999.99 EUR (RG-001)");
        }
    }
    
    /**
     * Mark prepayment as completed
     */
    public void complete() {
        if (this.status != PrepaymentStatus.PENDING) {
            throw new IllegalStateException("Can only complete pending prepayments");
        }
        this.status = PrepaymentStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * Mark prepayment as failed
     */
    public void fail() {
        if (this.status != PrepaymentStatus.PENDING) {
            throw new IllegalStateException("Can only fail pending prepayments");
        }
        this.status = PrepaymentStatus.FAILED;
    }
    
    /**
     * Check if prepayment is in terminal state
     */
    public boolean isTerminal() {
        return this.status == PrepaymentStatus.COMPLETED || 
               this.status == PrepaymentStatus.FAILED;
    }
}

// Made with Bob
