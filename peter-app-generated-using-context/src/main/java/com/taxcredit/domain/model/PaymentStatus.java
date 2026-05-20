package com.taxcredit.domain.model;

/**
 * Payment lifecycle states
 * FR-005: Initial state PaymentReceived, support processing, allocated, and failed states
 */
public enum PaymentStatus {
    RECEIVED,    // PaymentReceived - initial state
    PROCESSING,  // Being processed
    ALLOCATED,   // Successfully allocated to debt
    FAILED       // Processing failed
}

// Made with Bob
