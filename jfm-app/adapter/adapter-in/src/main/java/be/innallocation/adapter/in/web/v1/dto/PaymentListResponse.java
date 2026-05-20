package be.innallocation.adapter.in.web.v1.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * API DTO for payment list response.
 */
public record PaymentListResponse(
    String paymentId,
    String bankReference,
    BigDecimal amount,
    String currency,
    String status,
    LocalDate paymentDate,
    String debtorName,
    String debtorAccount,
    Instant createdAt
) {}

// Made with Bob