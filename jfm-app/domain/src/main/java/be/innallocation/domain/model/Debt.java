package be.innallocation.domain.model;

import be.innallocation.domain.common.DomainException;
import be.innallocation.domain.common.Money;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Debt Aggregate Root.
 * Represents a debt owed by a citizen.
 * 
 * Key characteristics:
 * - May have a structured_reference for payment matching
 * - Debts WITHOUT structured_reference auto-allocate from provision (RG-011)
 * - Lifecycle: PENDING → ACTIVE → PARTIALLY_PAID → SETTLED or CANCELLED
 * 
 * Business Rules:
 * - RG-004: Structured reference format required if present
 * - RG-011: Debt without reference uses provision
 * 
 * Optimistic locking via version field.
 */
@Getter
@EqualsAndHashCode(of = "id")
public class Debt {

    private static final Pattern STRUCTURED_REFERENCE_PATTERN = 
        Pattern.compile("^\\+\\+\\+\\d{3}/\\d{4}/\\d{5}\\+\\+\\+$");

    private final UUID id;
    private final String debtId;
    private final String citizenId;
    private final Optional<String> structuredReference;
    private final Money originalAmount;
    private Money currentBalance;
    private DebtStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    @Getter(AccessLevel.NONE)
    private int version;

    private Debt(UUID id, String debtId, String citizenId, Optional<String> structuredReference,
                 Money originalAmount, Money currentBalance, DebtStatus status,
                 Instant createdAt, Instant updatedAt, int version) {
        this.id = id;
        this.debtId = debtId;
        this.citizenId = citizenId;
        this.structuredReference = structuredReference;
        this.originalAmount = originalAmount;
        this.currentBalance = currentBalance;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    /**
     * Factory method to create a new Debt.
     * 
     * @param debtId Business identifier for the debt
     * @param citizenId Citizen identifier
     * @param structuredReference Optional structured reference for payment matching (RG-004)
     * @param amount Debt amount (must be positive)
     * @return New Debt instance
     * @throws DomainException if business rules are violated
     */
    public static Debt create(String debtId, String citizenId, 
                             Optional<String> structuredReference, Money amount) {
        validateDebtId(debtId);
        validateCitizenId(citizenId);
        validateStructuredReference(structuredReference); // RG-004
        validateAmount(amount);

        Instant now = Instant.now();
        
        // Debt starts as PENDING, becomes ACTIVE when ready for allocation
        return new Debt(
            UUID.randomUUID(),
            debtId,
            citizenId,
            structuredReference,
            amount,
            amount, // currentBalance starts equal to originalAmount
            DebtStatus.PENDING,
            now,
            now,
            0
        );
    }

    /**
     * Reconstitute a Debt from persistence.
     */
    public static Debt reconstitute(UUID id, String debtId, String citizenId,
                                    Optional<String> structuredReference,
                                    Money originalAmount, Money currentBalance,
                                    DebtStatus status, Instant createdAt, 
                                    Instant updatedAt, int version) {
        return new Debt(id, debtId, citizenId, structuredReference, originalAmount,
                       currentBalance, status, createdAt, updatedAt, version);
    }

    /**
     * Activate this debt, making it eligible for allocation.
     */
    public void activate() {
        if (status != DebtStatus.PENDING) {
            throw new DomainException(
                String.format("Cannot activate debt %s in status %s", debtId, status)
            );
        }
        this.status = DebtStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    /**
     * Apply an allocation to this debt, reducing the balance.
     * 
     * @param amount Amount to allocate (must be positive and ≤ currentBalance)
     * @throws DomainException if business rules are violated
     */
    public void applyAllocation(Money amount) {
        if (status != DebtStatus.ACTIVE && status != DebtStatus.PARTIALLY_PAID) {
            throw new DomainException(
                String.format("Cannot allocate to debt %s in status %s", debtId, status)
            );
        }

        validateAllocationAmount(amount);

        if (amount.isGreaterThan(currentBalance)) {
            throw new DomainException(
                String.format("Allocation amount %s %s exceeds debt balance %s %s",
                    amount.getAmount(), amount.getCurrencyCode(),
                    currentBalance.getAmount(), currentBalance.getCurrencyCode())
            );
        }

        Money newBalance = currentBalance.subtract(amount);
        this.currentBalance = newBalance;

        // Update status based on new balance
        if (newBalance.isZero()) {
            this.status = DebtStatus.SETTLED;
        } else {
            this.status = DebtStatus.PARTIALLY_PAID;
        }

        this.updatedAt = Instant.now();
    }

    /**
     * Cancel this debt.
     */
    public void cancel() {
        if (status == DebtStatus.SETTLED) {
            throw new DomainException(
                String.format("Cannot cancel debt %s that is already settled", debtId)
            );
        }
        this.status = DebtStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    /**
     * Check if this debt requires auto-allocation from provision.
     * RG-011: Debt without reference uses provision
     * 
     * @return true if debt has no structured reference
     */
    public boolean requiresAutoAllocation() {
        return structuredReference.isEmpty();
    }

    /**
     * Check if this debt is eligible for allocation.
     * 
     * @return true if debt is ACTIVE or PARTIALLY_PAID
     */
    public boolean isEligibleForAllocation() {
        return status == DebtStatus.ACTIVE || status == DebtStatus.PARTIALLY_PAID;
    }

    /**
     * Check if this debt is fully settled.
     * 
     * @return true if debt is SETTLED
     */
    public boolean isSettled() {
        return status == DebtStatus.SETTLED;
    }

    private static void validateDebtId(String debtId) {
        if (debtId == null || debtId.isBlank()) {
            throw new DomainException("Debt ID cannot be null or blank");
        }
    }

    private static void validateCitizenId(String citizenId) {
        if (citizenId == null || citizenId.isBlank()) {
            throw new DomainException("Citizen ID cannot be null or blank");
        }
    }

    private static void validateStructuredReference(Optional<String> structuredReference) {
        // RG-004: Structured reference format required if present
        structuredReference.ifPresent(ref -> {
            if (!STRUCTURED_REFERENCE_PATTERN.matcher(ref).matches()) {
                throw new DomainException(
                    String.format("Invalid structured reference format: %s (RG-004). " +
                        "Expected format: +++XXX/XXXX/XXXXX+++", ref)
                );
            }
        });
    }

    private static void validateAmount(Money amount) {
        if (amount == null) {
            throw new DomainException("Debt amount cannot be null");
        }
        if (!amount.isPositive()) {
            throw new DomainException("Debt amount must be positive");
        }
    }

    private void validateAllocationAmount(Money amount) {
        if (amount == null) {
            throw new DomainException("Allocation amount cannot be null");
        }
        if (!amount.isPositive()) {
            throw new DomainException("Allocation amount must be positive");
        }
        if (!amount.getCurrencyCode().equals(currentBalance.getCurrencyCode())) {
            throw new DomainException(
                String.format("Allocation currency %s does not match debt currency %s",
                    amount.getCurrencyCode(), currentBalance.getCurrencyCode())
            );
        }
    }

    public int getVersion() {
        return version;
    }

    public enum DebtStatus {
        PENDING,
        ACTIVE,
        PARTIALLY_PAID,
        SETTLED,
        CANCELLED
    }
}

// Made with Bob
