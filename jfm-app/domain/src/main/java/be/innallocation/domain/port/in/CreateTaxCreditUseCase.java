package be.innallocation.domain.port.in;

import be.innallocation.domain.common.Money;

/**
 * Inbound port for creating a tax credit.
 * Tax credits are standalone and feed the provision.
 */
public interface CreateTaxCreditUseCase {

    /**
     * Create a new tax credit for a citizen.
     * 
     * @param command Command containing tax credit details
     * @return Result containing the created tax credit ID
     */
    CreateTaxCreditResult execute(CreateTaxCreditCommand command);

    record CreateTaxCreditCommand(
        String taxCreditId,
        String citizenId,
        Money amount
    ) {}

    record CreateTaxCreditResult(
        String taxCreditId,
        String citizenId,
        Money amount,
        String status
    ) {}
}

// Made with Bob
