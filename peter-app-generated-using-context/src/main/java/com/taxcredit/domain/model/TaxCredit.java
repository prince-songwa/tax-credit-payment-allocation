package com.taxcredit.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * TaxCredit Aggregate Root
 * FR-001: Maintain a TaxCredit aggregate for each citizen
 * RG-002: Balance = Total prepayments - Total allocations
 */
@Entity
@Table(name = "tax_credits")
@Getter
@NoArgsConstructor
public class TaxCredit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String citizenId;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrepayments = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAllocations = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency = Currency.EUR;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaxCreditStatus status = TaxCreditStatus.ACTIVE;
    
    @Version
    private Long version;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public TaxCredit(String citizenId) {
        this.citizenId = citizenId;
        this.currentBalance = BigDecimal.ZERO;
        this.totalPrepayments = BigDecimal.ZERO;
        this.totalAllocations = BigDecimal.ZERO;
        this.currency = Currency.EUR;
        this.status = TaxCreditStatus.ACTIVE;
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
     * Add prepayment amount to tax credit
     * RG-002: Balance = Total prepayments - Total allocations
     */
    public void addPrepayment(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Prepayment amount must be positive");
        }
        this.totalPrepayments = this.totalPrepayments.add(amount);
        recalculateBalance();
        updateStatus();
    }
    
    /**
     * Add allocation amount (reduces balance)
     * RG-002: Balance = Total prepayments - Total allocations
     * RG-008: Allocation cannot exceed source balance
     */
    public void addAllocation(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Allocation amount must be positive");
        }
        if (amount.compareTo(this.currentBalance) > 0) {
            throw new IllegalArgumentException("Allocation amount exceeds available balance");
        }
        this.totalAllocations = this.totalAllocations.add(amount);
        recalculateBalance();
        updateStatus();
    }
    
    /**
     * Recalculate balance based on invariant
     * RG-002: Balance = Total prepayments - Total allocations
     */
    private void recalculateBalance() {
        this.currentBalance = this.totalPrepayments.subtract(this.totalAllocations);
    }
    
    /**
     * Update status based on balance
     */
    private void updateStatus() {
        if (this.status == TaxCreditStatus.SUSPENDED) {
            return; // Don't change if suspended
        }
        
        if (this.currentBalance.compareTo(BigDecimal.ZERO) == 0) {
            this.status = TaxCreditStatus.DEPLETED;
        } else {
            this.status = TaxCreditStatus.ACTIVE;
        }
    }
    
    /**
     * Check if tax credit has available balance
     */
    public boolean hasAvailableBalance() {
        return this.currentBalance.compareTo(BigDecimal.ZERO) > 0 
               && this.status == TaxCreditStatus.ACTIVE;
    }
    
    /**
     * Suspend tax credit
     */
    public void suspend() {
        this.status = TaxCreditStatus.SUSPENDED;
    }
    
    /**
     * Reactivate tax credit
     */
    public void reactivate() {
        if (this.currentBalance.compareTo(BigDecimal.ZERO) > 0) {
            this.status = TaxCreditStatus.ACTIVE;
        } else {
            this.status = TaxCreditStatus.DEPLETED;
        }
    }
}

// Made with Bob
