package com.taxcredit.domain.model;

/**
 * Debt lifecycle states
 * FR-008: Support debt lifecycle states DebtOutstanding, DebtPartiallyPaid, and DebtSettled
 */
public enum DebtStatus {
    OUTSTANDING,      // DebtOutstanding - initial state
    PARTIALLY_PAID,   // DebtPartiallyPaid - some allocations applied
    SETTLED           // DebtSettled - terminal state, fully paid
}

// Made with Bob
