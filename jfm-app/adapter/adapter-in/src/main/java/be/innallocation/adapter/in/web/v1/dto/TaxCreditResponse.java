package be.innallocation.adapter.in.web.v1.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * API DTO for tax credit response.
 */
public record TaxCreditResponse(
    String taxCreditId,
    String citizenId,
    BigDecimal amount,
    String currency,
    String status,
    Instant createdAt
) {}

// Made with Bob
