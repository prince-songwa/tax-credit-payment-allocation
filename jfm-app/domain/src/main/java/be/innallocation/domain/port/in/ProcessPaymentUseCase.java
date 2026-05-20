package be.innallocation.domain.port.in;

import be.innallocation.domain.common.Money;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Inbound port for processing a payment.
 * Payment with matching debt → allocated directly.
 * Payment without matching debt → goes to provision.
 */
public interface ProcessPaymentUseCase {

    /**
     * Process a payment received from a citizen.
     * If structured reference matches a debt, payment is allocated directly.
     * Otherwise, payment goes to provision.
     * 
     * @param command Command containing payment details
     * @return Result containing the processed payment details
     */
    ProcessPaymentResult execute(ProcessPaymentCommand command);

    record ProcessPaymentCommand(
        String paymentId,
        String bankReference,
        Optional<String> structuredReference,
        Money amount,
        LocalDate paymentDate,
        String debtorAccount,
        String debtorName
    ) {}

    record ProcessPaymentResult(
        String paymentId,
        String bankReference,
        Money amount,
        String status,
        Optional<String> allocatedToDebtId,
        boolean addedToProvision
    ) {}
}

// Made with Bob
