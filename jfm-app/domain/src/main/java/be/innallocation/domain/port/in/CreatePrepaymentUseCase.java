package be.innallocation.domain.port.in;

import be.innallocation.domain.common.Money;

/**
 * Inbound port for creating a prepayment.
 * Prepayments are standalone and feed the provision.
 */
public interface CreatePrepaymentUseCase {

    /**
     * Create a new prepayment for a citizen.
     * 
     * @param command Command containing prepayment details
     * @return Result containing the created prepayment ID
     */
    CreatePrepaymentResult execute(CreatePrepaymentCommand command);

    record CreatePrepaymentCommand(
        String prepaymentId,
        String citizenId,
        Money amount,
        String paymentReference,
        String idempotencyKey
    ) {}

    record CreatePrepaymentResult(
        String prepaymentId,
        String citizenId,
        Money amount,
        String status
    ) {}
}

// Made with Bob
