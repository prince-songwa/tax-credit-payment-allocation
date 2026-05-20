package be.innallocation.adapter.in.web.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * API DTO for creating a tax credit.
 */
public record CreateTaxCreditRequest(
    @NotBlank(message = "Tax credit ID is required")
    String taxCreditId,

    @NotBlank(message = "Citizen ID is required")
    String citizenId,

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    BigDecimal amount,

    @NotBlank(message = "Currency is required")
    String currency
) {}

// Made with Bob
