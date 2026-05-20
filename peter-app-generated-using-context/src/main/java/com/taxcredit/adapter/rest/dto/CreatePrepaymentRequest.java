package com.taxcredit.adapter.rest.dto;

import com.taxcredit.domain.model.PrepaymentType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO for creating a prepayment
 */
public record CreatePrepaymentRequest(
    @NotBlank(message = "Citizen ID is required")
    String citizenId,
    
    @NotNull(message = "Prepayment type is required")
    PrepaymentType type,
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    @DecimalMax(value = "999999.99", message = "Amount must not exceed 999,999.99")
    BigDecimal amount,
    
    @NotBlank(message = "Idempotency key is required")
    String idempotencyKey
) {}

// Made with Bob
