package com.taxcredit.domain.model;

/**
 * Allocation lifecycle states
 * FR-009: Initial state AllocationPending, terminal states applied and reversed
 */
public enum AllocationStatus {
    PENDING,   // AllocationPending - initial state
    APPLIED,   // Terminal state - successfully applied
    REVERSED   // Terminal state - allocation reversed
}

// Made with Bob
