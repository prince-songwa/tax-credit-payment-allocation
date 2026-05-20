package be.innallocation.application.service;

import be.innallocation.domain.common.DomainException;
import be.innallocation.domain.model.Provision;
import be.innallocation.domain.model.TaxCredit;
import be.innallocation.domain.port.in.CreateTaxCreditUseCase;
import be.innallocation.domain.port.out.ProvisionRepository;
import be.innallocation.domain.port.out.TaxCreditRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Application service implementing the CreateTaxCreditUseCase.
 * Orchestrates the creation of a tax credit and its application to provision.
 * 
 * Flow:
 * 1. Validate tax credit doesn't already exist
 * 2. Create tax credit
 * 3. Get or create provision for citizen
 * 4. Credit provision with tax credit amount
 * 5. Mark tax credit as applied
 */
@RequiredArgsConstructor
public class CreateTaxCreditService implements CreateTaxCreditUseCase {

    private final TaxCreditRepository taxCreditRepository;
    private final ProvisionRepository provisionRepository;

    @Override
    public CreateTaxCreditResult execute(CreateTaxCreditCommand command) {
        // Validate tax credit doesn't already exist
        if (taxCreditRepository.existsByTaxCreditId(command.taxCreditId())) {
            throw new DomainException(
                String.format("Tax credit with ID %s already exists", command.taxCreditId())
            );
        }

        // Create tax credit
        TaxCredit taxCredit = TaxCredit.create(
            command.taxCreditId(),
            command.citizenId(),
            command.amount()
        );

        // Save tax credit
        taxCredit = taxCreditRepository.save(taxCredit);

        // Get or create provision for citizen
        Provision provision = getOrCreateProvision(command.citizenId(), command.amount().getCurrencyCode());

        // Credit provision with tax credit amount
        provision.credit(command.amount());
        provisionRepository.save(provision);

        // Mark tax credit as applied
        taxCredit.markAsApplied();
        taxCredit = taxCreditRepository.save(taxCredit);

        return new CreateTaxCreditResult(
            taxCredit.getTaxCreditId(),
            taxCredit.getCitizenId(),
            taxCredit.getAmount(),
            taxCredit.getStatus().name()
        );
    }

    private Provision getOrCreateProvision(String citizenId, String currency) {
        return provisionRepository.findByCitizenId(citizenId)
            .orElseGet(() -> {
                String provisionId = "PROV-" + citizenId + "-" + UUID.randomUUID().toString().substring(0, 8);
                Provision newProvision = Provision.create(provisionId, citizenId, currency);
                return provisionRepository.save(newProvision);
            });
    }
}

// Made with Bob
