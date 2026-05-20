package be.innallocation.application.service;

import be.innallocation.domain.common.DomainException;
import be.innallocation.domain.model.Prepayment;
import be.innallocation.domain.model.Provision;
import be.innallocation.domain.port.in.CreatePrepaymentUseCase;
import be.innallocation.domain.port.out.PrepaymentRepository;
import be.innallocation.domain.port.out.ProvisionRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Application service implementing the CreatePrepaymentUseCase.
 * Orchestrates the creation of a prepayment and its application to provision.
 * 
 * Flow:
 * 1. Check idempotency (RG-003)
 * 2. Validate prepayment doesn't already exist
 * 3. Create prepayment
 * 4. Get or create provision for citizen
 * 5. Credit provision with prepayment amount
 * 6. Mark prepayment as applied
 */
@RequiredArgsConstructor
public class CreatePrepaymentService implements CreatePrepaymentUseCase {

    private final PrepaymentRepository prepaymentRepository;
    private final ProvisionRepository provisionRepository;

    @Override
    public CreatePrepaymentResult execute(CreatePrepaymentCommand command) {
        // RG-003: Check idempotency - if already processed, return existing
        if (prepaymentRepository.existsByIdempotencyKey(command.idempotencyKey())) {
            Prepayment existing = prepaymentRepository.findByIdempotencyKey(command.idempotencyKey())
                .orElseThrow(() -> new DomainException("Idempotency check failed"));
            
            return new CreatePrepaymentResult(
                existing.getPrepaymentId(),
                existing.getCitizenId(),
                existing.getAmount(),
                existing.getStatus().name()
            );
        }

        // Validate prepayment doesn't already exist by business ID
        if (prepaymentRepository.existsByPrepaymentId(command.prepaymentId())) {
            throw new DomainException(
                String.format("Prepayment with ID %s already exists", command.prepaymentId())
            );
        }

        // Create prepayment
        Prepayment prepayment = Prepayment.create(
            command.prepaymentId(),
            command.citizenId(),
            command.amount(),
            command.paymentReference(),
            command.idempotencyKey()
        );

        // Save prepayment
        prepayment = prepaymentRepository.save(prepayment);

        // Get or create provision for citizen
        Provision provision = getOrCreateProvision(command.citizenId(), command.amount().getCurrencyCode());

        // Credit provision with prepayment amount
        provision.credit(command.amount());
        provisionRepository.save(provision);

        // Mark prepayment as applied
        prepayment.markAsApplied();
        prepayment = prepaymentRepository.save(prepayment);

        return new CreatePrepaymentResult(
            prepayment.getPrepaymentId(),
            prepayment.getCitizenId(),
            prepayment.getAmount(),
            prepayment.getStatus().name()
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
