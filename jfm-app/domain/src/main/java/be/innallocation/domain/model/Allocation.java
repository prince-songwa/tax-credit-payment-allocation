package be.innallocation.domain.model;

import be.innallocation.domain.common.DomainException;
import be.innallocation.domain.common.Money;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Allocation Aggregate Root.
 * Links a source (Provision or Payment) to a Debt.
 * 
 * Represents the act of allocating funds to settle a debt.
 * 
 * Source types:
 * - PROVISION: Allocation from citizen's provision balance
 * - PAYMENT: Direct allocation from a payment to a debt
 * 
 * Lifecycle: PENDING → APPLIED
 */
@Getter
@EqualsAndHashCode(of = "id")
public class Allocation {

    private final UUID id;
    private final String allocationId;
    private final AllocationSourceType sourceType;
    private final UUID sourceId;
    private final UUID debtId;
    private final Money amount;
    private AllocationStatus status;
    private final LocalDate allocationDate;
    private Instant appliedDate;
    private final Instant createdAt;

    @Getter(AccessLevel.NONE)
    private int version;

    private Allocation(UUID id, String allocationId, AllocationSourceType sourceType,
                       UUID sourceId, UUID debtId, Money amount, AllocationStatus status,
                       LocalDate allocationDate, Instant appliedDate, Instant createdAt) {
        this.id = id;
        this.allocationId = allocationId;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.debtId = debtId;
        this.amount = amount;
        this.status = status;
        this.allocationDate = allocationDate;
        this.appliedDate = appliedDate;
        this.createdAt = createdAt;
        this.version = 0;
    }

    /**
     * Factory method to create a new Allocation from Provision.
     * 
     * @param allocationId Business identifier for the allocation
     * @param provisionId ID of the provision (source)
     * @param debtId ID of the debt (target)
     * @param amount Amount to allocate (must be positive)
     * @param allocationDate Date of allocation
     * @return New Allocation instance
     * @throws DomainException if business rules are violated
     */
    public static Allocation fromProvision(String allocationId, UUID provisionId,
                                          UUID debtId, Money amount, LocalDate allocationDate) {
        validateAllocationId(allocationId);
        validateSourceId(provisionId);
        validateDebtId(debtId);
        validateAmount(amount);
        validateAllocationDate(allocationDate);

        return new Allocation(
            UUID.randomUUID(),
            allocationId,
            AllocationSourceType.PROVISION,
            provisionId,
            debtId,
            amount,
            AllocationStatus.PENDING,
            allocationDate,
            null,
            Instant.now()
        );
    }

    /**
     * Factory method to create a new Allocation from Payment.
     * 
     * @param allocationId Business identifier for the allocation
     * @param paymentId ID of the payment (source)
     * @param debtId ID of the debt (target)
     * @param amount Amount to allocate (must be positive)
     * @param allocationDate Date of allocation
     * @return New Allocation instance
     * @throws DomainException if business rules are violated
     */
    public static Allocation fromPayment(String allocationId, UUID paymentId,
                                        UUID debtId, Money amount, LocalDate allocationDate) {
        validateAllocationId(allocationId);
        validateSourceId(paymentId);
        validateDebtId(debtId);
        validateAmount(amount);
        validateAllocationDate(allocationDate);

        return new Allocation(
            UUID.randomUUID(),
            allocationId,
            AllocationSourceType.PAYMENT,
            paymentId,
            debtId,
            amount,
            AllocationStatus.PENDING,
            allocationDate,
            null,
            Instant.now()
        );
    }

    /**
     * Reconstitute an Allocation from persistence.
     */
    public static Allocation reconstitute(UUID id, String allocationId,
                                         AllocationSourceType sourceType, UUID sourceId,
                                         UUID debtId, Money amount, AllocationStatus status,
                                         LocalDate allocationDate, Instant appliedDate,
                                         Instant createdAt) {
        return new Allocation(id, allocationId, sourceType, sourceId, debtId, amount,
                             status, allocationDate, appliedDate, createdAt);
    }

    /**
     * Mark this allocation as applied.
     * Records the timestamp when the allocation was actually applied.
     * 
     * @throws DomainException if allocation is not in PENDING status
     */
    public void markAsApplied() {
        if (status != AllocationStatus.PENDING) {
            throw new DomainException(
                String.format("Cannot apply allocation %s in status %s", allocationId, status)
            );
        }
        this.status = AllocationStatus.APPLIED;
        this.appliedDate = Instant.now();
    }

    /**
     * Cancel this allocation.
     * 
     * @throws DomainException if allocation is already applied
     */
    public void cancel() {
        if (status == AllocationStatus.APPLIED) {
            throw new DomainException(
                String.format("Cannot cancel allocation %s that has been applied", allocationId)
            );
        }
        this.status = AllocationStatus.CANCELLED;
    }

    /**
     * Check if this allocation is from provision.
     * 
     * @return true if source type is PROVISION
     */
    public boolean isFromProvision() {
        return sourceType == AllocationSourceType.PROVISION;
    }

    /**
     * Check if this allocation is from payment.
     * 
     * @return true if source type is PAYMENT
     */
    public boolean isFromPayment() {
        return sourceType == AllocationSourceType.PAYMENT;
    }

    private static void validateAllocationId(String allocationId) {
        if (allocationId == null || allocationId.isBlank()) {
            throw new DomainException("Allocation ID cannot be null or blank");
        }
    }

    private static void validateSourceId(UUID sourceId) {
        if (sourceId == null) {
            throw new DomainException("Source ID cannot be null");
        }
    }

    private static void validateDebtId(UUID debtId) {
        if (debtId == null) {
            throw new DomainException("Debt ID cannot be null");
        }
    }

    private static void validateAmount(Money amount) {
        if (amount == null) {
            throw new DomainException("Allocation amount cannot be null");
        }
        if (!amount.isPositive()) {
            throw new DomainException("Allocation amount must be positive");
        }
    }

    private static void validateAllocationDate(LocalDate allocationDate) {
        if (allocationDate == null) {
            throw new DomainException("Allocation date cannot be null");
        }
    }

    public enum AllocationSourceType {
        PROVISION,
        PAYMENT
    }

    public enum AllocationStatus {
        PENDING,
        APPLIED,
        CANCELLED
    }
}

// Made with Bob
