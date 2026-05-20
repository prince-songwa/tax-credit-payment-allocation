package com.taxcredit.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Payment Entity
 * FR-005: Receive and record bank transfer payments
 * RG-004: Structured reference format +++XXX/XXXX/XXXXX+++
 * RG-009: Unique bank reference, payment applied only once
 */
@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String bankReference;
    
    @Column(nullable = false)
    private String structuredReference;
    
    @Column(nullable = false)
    private String debtReference;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency = Currency.EUR;
    
    @Column(nullable = false)
    private LocalDate paymentDate;
    
    @Column(nullable = false)
    private String debtorAccount;
    
    @Column(nullable = false)
    private String debtorName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.RECEIVED;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public Payment(String bankReference, String structuredReference, String debtReference,
                   BigDecimal amount, LocalDate paymentDate, String debtorAccount, String debtorName) {
        validateStructuredReference(structuredReference);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        this.bankReference = bankReference;
        this.structuredReference = structuredReference;
        this.debtReference = debtReference;
        this.amount = amount;
        this.currency = Currency.EUR;
        this.paymentDate = paymentDate;
        this.debtorAccount = debtorAccount;
        this.debtorName = debtorName;
        this.status = PaymentStatus.RECEIVED;
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
     * Validate structured reference format
     * RG-004: Format +++XXX/XXXX/XXXXX+++
     */
    private void validateStructuredReference(String reference) {
        if (reference == null || !reference.matches("\\+\\+\\+\\d{3}/\\d{4}/\\d{5}\\+\\+\\+")) {
            throw new IllegalArgumentException(
                "Invalid structured reference format. Expected: +++XXX/XXXX/XXXXX+++ (RG-004)");
        }
    }
    
    /**
     * Mark payment as processing
     */
    public void startProcessing() {
        if (this.status != PaymentStatus.RECEIVED) {
            throw new IllegalStateException("Can only process received payments");
        }
        this.status = PaymentStatus.PROCESSING;
    }
    
    /**
     * Mark payment as allocated
     */
    public void markAllocated() {
        if (this.status != PaymentStatus.PROCESSING) {
            throw new IllegalStateException("Can only allocate processing payments");
        }
        this.status = PaymentStatus.ALLOCATED;
    }
    
    /**
     * Mark payment as failed
     */
    public void markFailed() {
        if (this.status == PaymentStatus.ALLOCATED) {
            throw new IllegalStateException("Cannot fail already allocated payment");
        }
        this.status = PaymentStatus.FAILED;
    }
    
    /**
     * Check if payment can be processed
     */
    public boolean canBeProcessed() {
        return this.status == PaymentStatus.RECEIVED;
    }
}

// Made with Bob
