package com.taxcredit.domain.port;

import com.taxcredit.domain.model.AccountingEntry;

import java.util.List;

/**
 * Port interface for AccountingEntry repository
 */
public interface AccountingEntryRepository {
    
    AccountingEntry save(AccountingEntry accountingEntry);
    
    List<AccountingEntry> saveAll(List<AccountingEntry> entries);
    
    List<AccountingEntry> findByTransactionId(String transactionId);
    
    List<AccountingEntry> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
}

// Made with Bob
