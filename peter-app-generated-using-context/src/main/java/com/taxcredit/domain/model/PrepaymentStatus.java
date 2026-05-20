package com.taxcredit.domain.model;

/**
 * Prepayment lifecycle states
 * FR-002: Initial state PrepaymentPending, terminal states completed or failed
 */
public enum PrepaymentStatus {
    PENDING,    // PrepaymentPending - initial state
    COMPLETED,  // Terminal state - successfully processed
    FAILED      // Terminal state - processing failed
}

// Made with Bob
