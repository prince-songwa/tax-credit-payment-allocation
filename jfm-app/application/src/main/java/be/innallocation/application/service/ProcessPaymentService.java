package be.innallocation.application.service;

import be.innallocation.domain.common.DomainException;
import be.innallocation.domain.common.Money;
import be.innallocation.domain.model.Allocation;
import be.innallocation.domain.model.Debt;
import be.innallocation.domain.model.Payment;
import be.innallocation.domain.model.Provision;
import be.innallocation.domain.port.in.ProcessPaymentUseCase;
import be.innallocation.domain.port.out.AllocationRepository;
import be.innallocation.domain.port.out.DebtRepository;
import be.innallocation.domain.port.out.PaymentRepository;
import be.innallocation.domain.port.out.ProvisionRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service implementing the ProcessPaymentUseCase.
 * Orchestrates payment processing with debt matching or provision funding.
 * 
 * Flow:
 * 1. Check for duplicate payment (bank_reference uniqueness)
 * 2. If structured reference present:
 *    - Try to match with debt
 *    - If match found: allocate payment directly to debt (RG-009)
 *    - If no match: add to provision
 * 3. If no structured reference: add to provision
 */
@RequiredArgsConstructor
public class ProcessPaymentService implements ProcessPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final DebtRepository debtRepository;
    private final ProvisionRepository provisionRepository;
    private final AllocationRepository allocationRepository;

    @Override
    public ProcessPaymentResult execute(ProcessPaymentCommand command) {
        // Check for duplicate payment
        if (paymentRepository.existsByBankReference(command.bankReference())) {
            throw new DomainException(
                String.format("Payment with bank reference %s already exists", command.bankReference())
            );
        }

        // Try to match payment with debt via structured reference
        Optional<Debt> matchedDebt = command.structuredReference()
            .flatMap(debtRepository::findByStructuredReference);

        if (matchedDebt.isPresent()) {
            // Payment matched to debt - allocate directly
            return processAllocatedPayment(command, matchedDebt.get());
        } else {
            // No match - add to provision
            return processUnallocatedPayment(command);
        }
    }

    private ProcessPaymentResult processAllocatedPayment(ProcessPaymentCommand command, Debt debt) {
        // Validate debt is eligible for allocation
        if (!debt.isEligibleForAllocation()) {
            throw new DomainException(
                String.format("Debt %s is not eligible for allocation (status: %s)",
                    debt.getDebtId(), debt.getStatus())
            );
        }

        // Create allocated payment
        Payment payment = Payment.createAllocated(
            command.paymentId(),
            command.bankReference(),
            command.structuredReference().orElseThrow(),
            debt.getId(),
            command.amount(),
            command.paymentDate(),
            command.debtorAccount(),
            command.debtorName()
        );

        payment = paymentRepository.save(payment);

        // Determine allocation amount (min of payment amount and debt balance)
        var allocationAmount = command.amount().isLessThanOrEqual(debt.getCurrentBalance())
            ? command.amount()
            : debt.getCurrentBalance();

        // Apply allocation to debt
        debt.applyAllocation(allocationAmount);
        debtRepository.save(debt);

        // Create allocation record
        String allocationId = "ALLOC-" + UUID.randomUUID().toString().substring(0, 8);
        Allocation allocation = Allocation.fromPayment(
            allocationId,
            payment.getId(),
            debt.getId(),
            allocationAmount,
            command.paymentDate()
        );
        allocation.markAsApplied();
        allocationRepository.save(allocation);

        // If payment amount exceeds debt balance, add remainder to provision
        boolean addedToProvision = false;
        if (command.amount().isGreaterThan(debt.getOriginalAmount())) {
            var remainder = command.amount().subtract(allocationAmount);
            addToProvision(debt.getCitizenId(), remainder);
            addedToProvision = true;
        }

        return new ProcessPaymentResult(
            payment.getPaymentId(),
            payment.getBankReference(),
            payment.getAmount(),
            payment.getStatus().name(),
            Optional.of(debt.getDebtId()),
            addedToProvision
        );
    }

    private ProcessPaymentResult processUnallocatedPayment(ProcessPaymentCommand command) {
        // Create unallocated payment
        Payment payment = Payment.createUnallocated(
            command.paymentId(),
            command.bankReference(),
            command.structuredReference(),
            command.amount(),
            command.paymentDate(),
            command.debtorAccount(),
            command.debtorName()
        );

        payment = paymentRepository.save(payment);

        // Extract citizen ID from debtor account or name (simplified - in real system would be more sophisticated)
        String citizenId = extractCitizenId(command);

        // Add to provision
        addToProvision(citizenId, command.amount());

        return new ProcessPaymentResult(
            payment.getPaymentId(),
            payment.getBankReference(),
            payment.getAmount(),
            payment.getStatus().name(),
            Optional.empty(),
            true
        );
    }

    private void addToProvision(String citizenId, Money amount) {
        // Get or create provision
        Provision provision = provisionRepository.findByCitizenId(citizenId)
            .orElseGet(() -> {
                String provisionId = "PROV-" + citizenId + "-" + UUID.randomUUID().toString().substring(0, 8);
                Provision newProvision = Provision.create(provisionId, citizenId, amount.getCurrencyCode());
                return provisionRepository.save(newProvision);
            });

        // Credit provision
        provision.credit(amount);
        provisionRepository.save(provision);
    }

    private String extractCitizenId(ProcessPaymentCommand command) {
        // Simplified extraction - in real system would use proper citizen identification
        // For now, use debtor account as citizen ID
        return command.debtorAccount();
    }
}

// Made with Bob
