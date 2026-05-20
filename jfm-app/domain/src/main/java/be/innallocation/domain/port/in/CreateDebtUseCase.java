package be.innallocation.domain.port.in;

import be.innallocation.domain.common.Money;

import java.util.Optional;

/**
 * Inbound port for creating a debt.
 * Debts without structured reference will auto-allocate from provision (RG-011).
 */
public interface CreateDebtUseCase {

    /**
     * Create a new debt for a citizen.
     * If no structured reference is provided, the debt will automatically
     * consume provision balance (RG-011).
     * 
     * @param command Command containing debt details
     * @return Result containing the created debt ID
     */
    CreateDebtResult execute(CreateDebtCommand command);

    record CreateDebtCommand(
        String debtId,
        String citizenId,
        Optional<String> structuredReference,
        Money amount
    ) {}

    record CreateDebtResult(
        String debtId,
        String citizenId,
        Optional<String> structuredReference,
        Money originalAmount,
        Money currentBalance,
        String status,
        boolean autoAllocated
    ) {}
}

// Made with Bob
