package com.taxcredit.domain.model;

/**
 * Debt type enumeration
 * FR-012: Priority logic - PENALTY before TAX_DEBT before ADMINISTRATIVE_FEE
 */
public enum DebtType {
    PENALTY(1),              // Highest priority
    TAX_DEBT(2),            // Medium priority
    ADMINISTRATIVE_FEE(3);  // Lowest priority
    
    private final int priority;
    
    DebtType(int priority) {
        this.priority = priority;
    }
    
    public int getPriority() {
        return priority;
    }
}

// Made with Bob
