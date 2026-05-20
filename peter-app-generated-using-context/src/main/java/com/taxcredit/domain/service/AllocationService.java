package com.taxcredit.domain.service;

import com.taxcredit.domain.model.*;
import com.taxcredit.domain.port.AllocationRepository;
import com.taxcredit.domain.port.DebtRepository;
import com.taxcredit.domain.port.TaxCreditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Domain Service for Allocation Operations
 * FR-010: Automatically allocate tax credit to debt
 * FR-012: Apply allocation priority rules
 * RG-005: Oldest debts first, then by debt type priority
 */
@Service
public class AllocationService {
    
    private final AllocationRepository allocationRepository;
    private final DebtRepository debtRepository;
    private final TaxCreditRepository taxCreditRepository;
    private final AccountingService accountingService;
    
    public AllocationService(AllocationRepository allocationRepository,
                           DebtRepository debtRepository,
                           TaxCreditRepository taxCreditRepository,
                           AccountingService accountingService) {
        this.allocationRepository = allocationRepository;
        this.debtRepository = debtRepository;
        this.taxCreditRepository = taxCreditRepository;
        this.accountingService = accountingService;
    }
    
    /**
     * Automatically allocate tax credit to outstanding debts
     * FR-010: AllocateTaxCreditToDebt action
     * FR-012: Priority-based allocation
     */
    @Transactional
    public List<Allocation> allocateTaxCreditToDebts(String citizenId) {
        // Get tax credit
        TaxCredit taxCredit = taxCreditRepository.findByCitizenId(citizenId)
            .orElseThrow(() -> new IllegalArgumentException("Tax credit not found for citizen: " + citizenId));
        
        // Validate tax credit has available balance
        if (!taxCredit.hasAvailableBalance()) {
            throw new IllegalStateException("Tax credit has no available balance");
        }
        
        // Get outstanding debts sorted by priority
        List<Debt> outstandingDebts = getOutstandingDebtsByPriority(citizenId);
        
        if (outstandingDebts.isEmpty()) {
            throw new IllegalStateException("No outstanding debts found for citizen: " + citizenId);
        }
        
        List<Allocation> allocations = new ArrayList<>();
        BigDecimal availableBalance = taxCredit.getCurrentBalance();
        
        // Allocate to debts in priority order
        for (Debt debt : outstandingDebts) {
            if (availableBalance.compareTo(BigDecimal.ZERO) <= 0) {
                break; // No more balance available
            }
            
            // Determine allocation amount (full debt or partial)
            BigDecimal allocationAmount = availableBalance.compareTo(debt.getRemainingBalance()) >= 0
                ? debt.getRemainingBalance()
                : availableBalance;
            
            // Create allocation
            Allocation allocation = createAllocation(
                AllocationSourceType.TAX_CREDIT,
                taxCredit.getId(),
                debt.getId(),
                citizenId,
                allocationAmount
            );
            
            // Apply allocation
            applyAllocation(allocation, taxCredit, debt);
            
            allocations.add(allocation);
            availableBalance = availableBalance.subtract(allocationAmount);
        }
        
        return allocations;
    }
    
    /**
     * Create allocation from payment to debt
     * FR-007: Process bank transfer payment allocation
     */
    @Transactional
    public Allocation allocatePaymentToDebt(Long paymentId, Long debtId, String citizenId, BigDecimal amount) {
        Debt debt = debtRepository.findById(debtId)
            .orElseThrow(() -> new IllegalArgumentException("Debt not found: " + debtId));
        
        if (!debt.isPayable()) {
            throw new IllegalStateException("Debt is not payable (already settled)");
        }
        
        if (amount.compareTo(debt.getRemainingBalance()) > 0) {
            throw new IllegalArgumentException("Allocation amount exceeds debt balance");
        }
        
        Allocation allocation = createAllocation(
            AllocationSourceType.PAYMENT,
            paymentId,
            debtId,
            citizenId,
            amount
        );
        
        // Apply allocation to debt
        debt.applyAllocation(amount);
        debtRepository.save(debt);
        
        // Mark allocation as applied
        allocation.apply();
        allocationRepository.save(allocation);
        
        // Generate accounting entries
        accountingService.generateAllocationEntries(allocation.getId(), amount);
        
        return allocation;
    }
    
    /**
     * Apply allocation to tax credit and debt
     * FR-011: ApplyAllocation action
     */
    private void applyAllocation(Allocation allocation, TaxCredit taxCredit, Debt debt) {
        // Update tax credit
        taxCredit.addAllocation(allocation.getAmount());
        taxCreditRepository.save(taxCredit);
        
        // Update debt
        debt.applyAllocation(allocation.getAmount());
        debtRepository.save(debt);
        
        // Mark allocation as applied
        allocation.apply();
        allocationRepository.save(allocation);
        
        // Generate accounting entries
        accountingService.generateAllocationEntries(allocation.getId(), allocation.getAmount());
    }
    
    /**
     * Create allocation entity
     */
    private Allocation createAllocation(AllocationSourceType sourceType, Long sourceId, 
                                       Long debtId, String citizenId, BigDecimal amount) {
        String reference = "ALLOC-" + UUID.randomUUID().toString();
        Allocation allocation = new Allocation(reference, sourceType, sourceId, debtId, citizenId, amount);
        return allocationRepository.save(allocation);
    }
    
    /**
     * Get outstanding debts sorted by priority
     * FR-012: Priority logic - oldest debts first, then by debt type priority, then by creation date
     */
    private List<Debt> getOutstandingDebtsByPriority(String citizenId) {
        List<Debt> debts = debtRepository.findByCitizenIdAndStatus(citizenId, DebtStatus.OUTSTANDING);
        debts.addAll(debtRepository.findByCitizenIdAndStatus(citizenId, DebtStatus.PARTIALLY_PAID));
        
        // Sort by: due date (oldest first), then debt type priority, then creation date
        debts.sort(Comparator
            .comparing(Debt::getDueDate)
            .thenComparing(Debt::getPriority)
            .thenComparing(Debt::getCreatedAt));
        
        return debts;
    }
}

// Made with Bob
