package be.innallocation.adapter.in.web.v1.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * API DTO for debt response.
 */
public record DebtResponse(
    String debtId,
    String citizenId,
    String structuredReference,
    BigDecimal originalAmount,
    BigDecimal currentBalance,
    String currency,
    String status,
    boolean autoAllocated,
    Instant createdAt
) {}

// Made with Bob
