package com.taxcredit.application.usecase;

import com.taxcredit.domain.model.Prepayment;
import com.taxcredit.domain.model.PrepaymentType;
import com.taxcredit.domain.model.TaxCredit;
import com.taxcredit.domain.port.PrepaymentRepository;
import com.taxcredit.domain.port.TaxCreditRepository;
import com.taxcredit.domain.service.AccountingService;
import com.taxcredit.domain.service.AllocationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Use Case: Create Prepayment
 * FR-002: Accept citizen prepayments
 * FR-003: Process prepayment creation flow
 * RG-003: Idempotency key ensures duplicate prevention
 */
@Service
public class CreatePrepaymentUseCase {
    
    private final PrepaymentRepository prepaymentRepository;
    private final TaxCreditRepository taxCreditRepository;
    private final AccountingService accountingService;
    private final AllocationService allocationService;
    
    public CreatePrepaymentUseCase(PrepaymentRepository prepaymentRepository,
                                  TaxCreditRepository taxCreditRepository,
                                  AccountingService accountingService,
                                  AllocationService allocationService) {
        this.prepaymentRepository = prepaymentRepository;
        this.taxCreditRepository = taxCreditRepository;
        this.accountingService = accountingService;
        this.allocationService = allocationService;
    }
    
    @Transactional
    public Prepayment execute(String citizenId, PrepaymentType type, BigDecimal amount, String idempotencyKey) {
        // Check idempotency
        if (prepaymentRepository.existsByIdempotencyKey(idempotencyKey)) {
            return prepaymentRepository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new IllegalStateException("Idempotency key exists but prepayment not found"));
        }
        
        // Get or create tax credit
        TaxCredit taxCredit = taxCreditRepository.findByCitizenId(citizenId)
            .orElseGet(() -> {
                TaxCredit newTaxCredit = new TaxCredit(citizenId);
                return taxCreditRepository.save(newTaxCredit);
            });
        
        // Create prepayment
        String paymentReference = "PREP-" + UUID.randomUUID().toString();
        Prepayment prepayment = new Prepayment(
            paymentReference,
            idempotencyKey,
            citizenId,
            taxCredit.getId(),
            type,
            amount
        );
        prepayment = prepaymentRepository.save(prepayment);
        
        // Update tax credit balance
        taxCredit.addPrepayment(amount);
        taxCreditRepository.save(taxCredit);
        
        // Complete prepayment
        prepayment.complete();
        prepayment = prepaymentRepository.save(prepayment);
        
        // Generate accounting entries
        accountingService.generatePrepaymentEntries(prepayment.getId(), amount);
        
        // Trigger automatic allocation if outstanding debts exist
        try {
            allocationService.allocateTaxCreditToDebts(citizenId);
        } catch (IllegalStateException e) {
            // No outstanding debts or no balance - this is acceptable
        }
        
        return prepayment;
    }
}

// Made with Bob
