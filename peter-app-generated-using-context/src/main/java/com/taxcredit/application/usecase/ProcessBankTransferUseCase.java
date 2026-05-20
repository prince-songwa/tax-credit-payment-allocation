package com.taxcredit.application.usecase;

import com.taxcredit.domain.model.Debt;
import com.taxcredit.domain.model.Payment;
import com.taxcredit.domain.port.DebtRepository;
import com.taxcredit.domain.port.PaymentRepository;
import com.taxcredit.domain.service.AccountingService;
import com.taxcredit.domain.service.AllocationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Use Case: Process Bank Transfer
 * FR-006: Parse structured reference from bank transfer
 * FR-007: Process bank transfer payment allocation
 * RG-004: Structured reference format validation
 */
@Service
public class ProcessBankTransferUseCase {
    
    private final PaymentRepository paymentRepository;
    private final DebtRepository debtRepository;
    private final AccountingService accountingService;
    private final AllocationService allocationService;
    
    public ProcessBankTransferUseCase(PaymentRepository paymentRepository,
                                     DebtRepository debtRepository,
                                     AccountingService accountingService,
                                     AllocationService allocationService) {
        this.paymentRepository = paymentRepository;
        this.debtRepository = debtRepository;
        this.accountingService = accountingService;
        this.allocationService = allocationService;
    }
    
    @Transactional
    public Payment execute(String bankReference, String structuredReference, 
                          BigDecimal amount, LocalDate paymentDate,
                          String debtorAccount, String debtorName) {
        // Check for duplicate bank reference
        if (paymentRepository.existsByBankReference(bankReference)) {
            return paymentRepository.findByBankReference(bankReference)
                .orElseThrow(() -> new IllegalStateException("Bank reference exists but payment not found"));
        }
        
        // Parse debt reference from structured reference
        String debtReference = parseDebtReference(structuredReference);
        
        // Find debt
        Debt debt = debtRepository.findByDebtReference(debtReference)
            .orElseThrow(() -> new IllegalArgumentException("Debt not found: " + debtReference));
        
        if (!debt.isPayable()) {
            throw new IllegalStateException("Debt is not payable (already settled)");
        }
        
        // Create payment
        Payment payment = new Payment(
            bankReference,
            structuredReference,
            debtReference,
            amount,
            paymentDate,
            debtorAccount,
            debtorName
        );
        payment = paymentRepository.save(payment);
        
        // Start processing
        payment.startProcessing();
        payment = paymentRepository.save(payment);
        
        // Generate accounting entries for payment
        accountingService.generatePaymentEntries(payment.getId(), amount);
        
        // Allocate payment to debt
        allocationService.allocatePaymentToDebt(
            payment.getId(),
            debt.getId(),
            debt.getCitizenId(),
            amount
        );
        
        // Mark payment as allocated
        payment.markAllocated();
        payment = paymentRepository.save(payment);
        
        return payment;
    }
    
    /**
     * Parse debt reference from structured reference
     * Format: +++XXX/XXXX/XXXXX+++ -> DEBT-XXX-XXXX-XXXXX
     */
    private String parseDebtReference(String structuredReference) {
        // Remove +++ markers and replace / with -
        String cleaned = structuredReference.replace("+++", "").replace("/", "-");
        return "DEBT-" + cleaned;
    }
}

// Made with Bob
