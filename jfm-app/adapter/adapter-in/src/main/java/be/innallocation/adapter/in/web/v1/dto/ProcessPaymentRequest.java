package be.innallocation.adapter.in.web.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * API DTO for processing a payment.
 */
public record ProcessPaymentRequest(
    @NotBlank(message = "Bank reference is required")
    String bankReference,

    @Pattern(regexp = "^\\+\\+\\+\\d{3}/\\d{4}/\\d{5}\\+\\+\\+$", 
             message = "Structured reference must match format +++XXX/XXXX/XXXXX+++")
    String structuredReference,

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    BigDecimal amount,

    @NotBlank(message = "Currency is required")
    String currency,

    @NotBlank(message = "Payment date is required")
    String paymentDate,

    @NotBlank(message = "Debtor account is required")
    String debtorAccount,

    @NotBlank(message = "Debtor name is required")
    String debtorName
) {}

// Made with Bob