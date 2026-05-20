package com.taxcredit.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Allocation Entity
 * FR-009: Create and track allocations (assignment of payment or tax credit to debt)
 * FR-011: Apply allocations to debts
 */
@Entity
@Table(name = "allocations")
@Getter
@NoArgsConstructor
public class Allocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String allocationReference;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllocationSourceType sourceType;
    
    @Column(nullable = false)
    private Long sourceId;
    
    @Column(nullable = false)
    private Long debtId;
    
    @Column(nullable = false)
    private String citizenId;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency = Currency.EUR;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllocationStatus status = AllocationStatus.PENDING;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    private LocalDateTime appliedAt;
    
    public Allocation(String allocationReference, AllocationSourceType sourceType, 
                      Long sourceId, Long debtId, String citizenId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Allocation amount must be positive");
        }
        this.allocationReference = allocationReference;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.debtId = debtId;
        this.citizenId = citizenId;
        this.amount = amount;
        this.currency = Currency.EUR;
        this.status = AllocationStatus.PENDING;
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
     * Apply allocation
     * FR-011: Change status to APPLIED, becomes immutable
     */
    public void apply() {
        if (this.status != AllocationStatus.PENDING) {
            throw new IllegalStateException("Can only apply pending allocations");
        }
        this.status = AllocationStatus.APPLIED;
        this.appliedAt = LocalDateTime.now();
    }
    
    /**
     * Reverse allocation
     */
    public void reverse() {
        if (this.status != AllocationStatus.APPLIED) {
            throw new IllegalStateException("Can only reverse applied allocations");
        }
        this.status = AllocationStatus.REVERSED;
    }
    
    /**
     * Check if allocation is pending
     */
    public boolean isPending() {
        return this.status == AllocationStatus.PENDING;
    }
    
    /**
     * Check if allocation is applied (immutable)
     */
    public boolean isApplied() {
        return this.status == AllocationStatus.APPLIED;
    }
    
    /**
     * Check if allocation is in terminal state
     */
    public boolean isTerminal() {
        return this.status == AllocationStatus.APPLIED || 
               this.status == AllocationStatus.REVERSED;
    }
}

// Made with Bob
