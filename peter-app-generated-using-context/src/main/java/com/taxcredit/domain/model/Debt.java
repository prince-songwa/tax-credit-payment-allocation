package com.taxcredit.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Debt Aggregate
 * FR-008: Manage debt lifecycle and balances
 * Balance = Original amount - Sum of allocations
 */
@Entity
@Table(name = "debts")
@Getter
@NoArgsConstructor
public class Debt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String debtReference;
    
    @Column(nullable = false)
    private String citizenId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DebtType debtType;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal originalAmount;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal remainingBalance;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency = Currency.EUR;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DebtStatus status = DebtStatus.OUTSTANDING;
    
    @Column(nullable = false)
    private LocalDate dueDate;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    private LocalDateTime settledAt;
    
    public Debt(String debtReference, String citizenId, DebtType debtType, 
                BigDecimal amount, LocalDate dueDate) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debt amount must be positive");
        }
        this.debtReference = debtReference;
        this.citizenId = citizenId;
        this.debtType = debtType;
        this.originalAmount = amount;
        this.remainingBalance = amount;
        this.currency = Currency.EUR;
        this.status = DebtStatus.OUTSTANDING;
        this.dueDate = dueDate;
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
     * Apply allocation to debt
     * FR-011: Reduce debt balance, update status when settled
     */
    public void applyAllocation(BigDecimal amount) {
        if (this.status == DebtStatus.SETTLED) {
            throw new IllegalStateException("Cannot allocate to settled debt");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Allocation amount must be positive");
        }
        
        if (amount.compareTo(this.remainingBalance) > 0) {
            throw new IllegalArgumentException("Allocation amount exceeds debt balance");
        }
        
        this.remainingBalance = this.remainingBalance.subtract(amount);
        updateStatus();
    }
    
    /**
     * Update debt status based on remaining balance
     */
    private void updateStatus() {
        if (this.remainingBalance.compareTo(BigDecimal.ZERO) == 0) {
            this.status = DebtStatus.SETTLED;
            this.settledAt = LocalDateTime.now();
        } else if (this.remainingBalance.compareTo(this.originalAmount) < 0) {
            this.status = DebtStatus.PARTIALLY_PAID;
        } else {
            this.status = DebtStatus.OUTSTANDING;
        }
    }
    
    /**
     * Check if debt is payable (not settled)
     */
    public boolean isPayable() {
        return this.status != DebtStatus.SETTLED;
    }
    
    /**
     * Check if debt is settled
     */
    public boolean isSettled() {
        return this.status == DebtStatus.SETTLED;
    }
    
    /**
     * Get debt priority for allocation ordering
     * FR-012: Priority logic for allocation
     */
    public int getPriority() {
        return this.debtType.getPriority();
    }
}

// Made with Bob
