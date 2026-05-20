package be.innallocation.adapter.in.web.v1.dto;

import java.math.BigDecimal;

/**
 * API DTO for payment response.
 */
public record PaymentResponse(
    String paymentId,
    String bankReference,
    BigDecimal amount,
    String currency,
    String status,
    String allocatedToDebtId,
    boolean addedToProvision
) {}

// Made with Bob