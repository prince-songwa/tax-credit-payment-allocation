package be.innallocation.application.service;

import be.innallocation.domain.common.DomainException;
import be.innallocation.domain.model.Allocation;
import be.innallocation.domain.model.Debt;
import be.innallocation.domain.model.Provision;
import be.innallocation.domain.port.in.CreateDebtUseCase;
import be.innallocation.domain.port.out.AllocationRepository;
import be.innallocation.domain.port.out.DebtRepository;
import be.innallocation.domain.port.out.ProvisionRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Application service implementing the CreateDebtUseCase.
 * Orchestrates the creation of a debt and auto-allocation from provision if needed.
 * 
 * Flow:
 * 1. Validate debt doesn't already exist
 * 2. Validate structured reference uniqueness if present
 * 3. Create debt
 * 4. Activate debt
 * 5. If debt has NO structured reference (RG-011):
 *    - Auto-allocate from provision
 *    - Create allocation record
 */
@RequiredArgsConstructor
public class CreateDebtService implements CreateDebtUseCase {

    private final DebtRepository debtRepository;
    private final ProvisionRepository provisionRepository;
    private final AllocationRepository allocationRepository;

    @Override
    public CreateDebtResult execute(CreateDebtCommand command) {
        // Validate debt doesn't already exist
        if (debtRepository.existsByDebtId(command.debtId())) {
            throw new DomainException(
                String.format("Debt with ID %s already exists", command.debtId())
            );
        }

        // Validate structured reference uniqueness if present
        command.structuredReference().ifPresent(ref -> {
            if (debtRepository.existsByStructuredReference(ref)) {
                throw new DomainException(
                    String.format("Debt with structured reference %s already exists", ref)
                );
            }
        });

        // Create debt
        Debt debt = Debt.create(
            command.debtId(),
            command.citizenId(),
            command.structuredReference(),
            command.amount()
        );

        // Activate debt
        debt.activate();

        // Save debt
        debt = debtRepository.save(debt);

        boolean autoAllocated = false;

        // RG-011: Debt without reference uses provision
        if (debt.requiresAutoAllocation()) {
            autoAllocated = tryAutoAllocateFromProvision(debt);
        }

        return new CreateDebtResult(
            debt.getDebtId(),
            debt.getCitizenId(),
            debt.getStructuredReference(),
            debt.getOriginalAmount(),
            debt.getCurrentBalance(),
            debt.getStatus().name(),
            autoAllocated
        );
    }

    private boolean tryAutoAllocateFromProvision(Debt debt) {
        // Get provision for citizen
        Provision provision = provisionRepository.findByCitizenId(debt.getCitizenId())
            .orElse(null);

        if (provision == null) {
            // No provision exists - debt remains unpaid
            return false;
        }

        // Check if provision has sufficient balance
        if (!provision.hasSufficientBalance(debt.getCurrentBalance())) {
            // Insufficient balance - debt remains unpaid (or partially paid if we implement partial allocation)
            return false;
        }

        // Allocate from provision
        provision.allocate(debt.getCurrentBalance());
        provisionRepository.save(provision);

        // Apply allocation to debt
        debt.applyAllocation(debt.getCurrentBalance());
        debtRepository.save(debt);

        // Create allocation record
        String allocationId = "ALLOC-" + UUID.randomUUID().toString().substring(0, 8);
        Allocation allocation = Allocation.fromProvision(
            allocationId,
            provision.getId(),
            debt.getId(),
            debt.getOriginalAmount(),
            LocalDate.now()
        );
        allocation.markAsApplied();
        allocationRepository.save(allocation);

        return true;
    }
}

// Made with Bob
