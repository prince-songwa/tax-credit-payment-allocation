package com.taxcredit.domain.model;

/**
 * Allocation source type
 * FR-009: Support sourceType values of TAX_CREDIT and PAYMENT
 */
public enum AllocationSourceType {
    TAX_CREDIT,  // Allocation from tax credit balance
    PAYMENT      // Allocation from bank transfer payment
}

// Made with Bob
