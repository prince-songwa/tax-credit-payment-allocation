package com.taxcredit.adapter.rest.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for processing bank transfer
 */
public record ProcessBankTransferRequest(
    @NotBlank(message = "Bank reference is required")
    String bankReference,
    
    @NotBlank(message = "Structured reference is required")
    @Pattern(regexp = "\\+\\+\\+\\d{3}/\\d{4}/\\d{5}\\+\\+\\+", 
             message = "Structured reference must match format +++XXX/XXXX/XXXXX+++")
    String structuredReference,
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    BigDecimal amount,
    
    @NotNull(message = "Payment date is required")
    LocalDate paymentDate,
    
    @NotBlank(message = "Debtor account is required")
    String debtorAccount,
    
    @NotBlank(message = "Debtor name is required")
    String debtorName
) {}

// Made with Bob
