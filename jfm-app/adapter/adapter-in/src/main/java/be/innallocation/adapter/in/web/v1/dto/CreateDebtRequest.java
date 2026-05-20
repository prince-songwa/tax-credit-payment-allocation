package be.innallocation.adapter.in.web.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * API DTO for creating a debt.
 */
public record CreateDebtRequest(
    @NotBlank(message = "Debt ID is required")
    String debtId,

    @NotBlank(message = "Citizen ID is required")
    String citizenId,

    @Pattern(regexp = "^\\+\\+\\+\\d{3}/\\d{4}/\\d{5}\\+\\+\\+$", 
             message = "Structured reference must match format +++XXX/XXXX/XXXXX+++")
    String structuredReference,

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    BigDecimal amount,

    @NotBlank(message = "Currency is required")
    String currency
) {}

// Made with Bob
