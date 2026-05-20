package com.taxcredit.domain.model;

/**
 * Tax Credit lifecycle states
 * FR-001: Support state transitions for active, depleted, and suspended
 */
public enum TaxCreditStatus {
    ACTIVE,      // TaxCreditActive - initial state
    DEPLETED,    // Balance is zero
    SUSPENDED    // Administrative suspension
}

// Made with Bob
