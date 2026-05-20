package com.taxcredit.domain.service;

import com.taxcredit.domain.model.AccountingEntry;
import com.taxcredit.domain.port.AccountingEntryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain Service for Accounting Operations
 * FR-013: Generate balanced accounting entries
 * RG-006: Total debit equals total credit
 */
@Service
public class AccountingService {
    
    private final AccountingEntryRepository accountingEntryRepository;
    
    public AccountingService(AccountingEntryRepository accountingEntryRepository) {
        this.accountingEntryRepository = accountingEntryRepository;
    }
    
    /**
     * Generate accounting entries for prepayment
     * DEBIT: Bank Account
     * CREDIT: Tax Credit Liability
     */
    public List<AccountingEntry> generatePrepaymentEntries(Long prepaymentId, BigDecimal amount) {
        String transactionId = "PREP-" + UUID.randomUUID().toString();
        List<AccountingEntry> entries = new ArrayList<>();
        
        // Debit: Bank Account
        entries.add(new AccountingEntry(
            transactionId,
            "1000",
            "Bank Account",
            "DEBIT",
            amount,
            "Prepayment received",
            "PREPAYMENT",
            prepaymentId
        ));
        
        // Credit: Tax Credit Liability
        entries.add(new AccountingEntry(
            transactionId,
            "2100",
            "Tax Credit Liability",
            "CREDIT",
            amount,
            "Tax credit increased",
            "PREPAYMENT",
            prepaymentId
        ));
        
        validateBalance(entries);
        return accountingEntryRepository.saveAll(entries);
    }
    
    /**
     * Generate accounting entries for bank transfer payment
     * DEBIT: Bank Account
     * CREDIT: Debt Payable
     */
    public List<AccountingEntry> generatePaymentEntries(Long paymentId, BigDecimal amount) {
        String transactionId = "PAY-" + UUID.randomUUID().toString();
        List<AccountingEntry> entries = new ArrayList<>();
        
        // Debit: Bank Account
        entries.add(new AccountingEntry(
            transactionId,
            "1000",
            "Bank Account",
            "DEBIT",
            amount,
            "Payment received",
            "PAYMENT",
            paymentId
        ));
        
        // Credit: Debt Payable
        entries.add(new AccountingEntry(
            transactionId,
            "2200",
            "Debt Payable",
            "CREDIT",
            amount,
            "Debt payment received",
            "PAYMENT",
            paymentId
        ));
        
        validateBalance(entries);
        return accountingEntryRepository.saveAll(entries);
    }
    
    /**
     * Generate accounting entries for tax credit allocation to debt
     * DEBIT: Tax Credit Liability
     * CREDIT: Debt Payable
     */
    public List<AccountingEntry> generateAllocationEntries(Long allocationId, BigDecimal amount) {
        String transactionId = "ALLOC-" + UUID.randomUUID().toString();
        List<AccountingEntry> entries = new ArrayList<>();
        
        // Debit: Tax Credit Liability
        entries.add(new AccountingEntry(
            transactionId,
            "2100",
            "Tax Credit Liability",
            "DEBIT",
            amount,
            "Tax credit allocated to debt",
            "ALLOCATION",
            allocationId
        ));
        
        // Credit: Debt Payable
        entries.add(new AccountingEntry(
            transactionId,
            "2200",
            "Debt Payable",
            "CREDIT",
            amount,
            "Debt reduced by allocation",
            "ALLOCATION",
            allocationId
        ));
        
        validateBalance(entries);
        return accountingEntryRepository.saveAll(entries);
    }
    
    /**
     * Validate that total debits equal total credits
     * RG-006: Double-entry bookkeeping rule
     */
    private void validateBalance(List<AccountingEntry> entries) {
        BigDecimal totalDebit = entries.stream()
            .filter(AccountingEntry::isDebit)
            .map(AccountingEntry::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCredit = entries.stream()
            .filter(AccountingEntry::isCredit)
            .map(AccountingEntry::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new IllegalStateException(
                "Accounting entries not balanced: Debit=" + totalDebit + ", Credit=" + totalCredit + " (RG-006)");
        }
    }
}

// Made with Bob
