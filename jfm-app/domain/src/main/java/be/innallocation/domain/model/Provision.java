package be.innallocation.domain.model;

import be.innallocation.domain.common.DomainException;
import be.innallocation.domain.common.Money;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Provision Aggregate Root - THE FINANCIAL PIVOT.
 * Central funding aggregate that holds the liability for a citizen.
 * 
 * Fed by:
 * - TaxCredit
 * - Prepayment
 * - Unallocated Payment
 * 
 * Used to settle debts through allocations.
 * 
 * CRITICAL INVARIANTS:
 * - Provision balance = total_credits - allocated_amounts (NOT using total_debits)
 * - Provision balance ≥ 0 (RG-002)
 * - One provision per citizen (enforced by UNIQUE constraint on citizen_id)
 * 
 * Optimistic locking via version field to prevent concurrent balance violations.
 */
@Getter
@EqualsAndHashCode(of = "id")
public class Provision {

    private final UUID id;
    private final String provisionId;
    private final String citizenId;
    private Money currentBalance;
    private Money totalCredits;
    private final Instant createdAt;
    private Instant updatedAt;

    @Getter(AccessLevel.NONE)
    private int version;

    private Provision(UUID id, String provisionId, String citizenId, Money currentBalance,
                      Money totalCredits, Instant createdAt, Instant updatedAt, int version) {
        this.id = id;
        this.provisionId = provisionId;
        this.citizenId = citizenId;
        this.currentBalance = currentBalance;
        this.totalCredits = totalCredits;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    /**
     * Factory method to create a new Provision for a citizen.
     * Starts with zero balance.
     * 
     * @param provisionId Business identifier for the provision
     * @param citizenId Citizen identifier (must be unique)
     * @param currency Currency for this provision
     * @return New Provision instance
     * @throws DomainException if business rules are violated
     */
    public static Provision create(String provisionId, String citizenId, String currency) {
        validateProvisionId(provisionId);
        validateCitizenId(citizenId);

        Money zero = Money.zero(currency);
        Instant now = Instant.now();

        return new Provision(
            UUID.randomUUID(),
            provisionId,
            citizenId,
            zero,
            zero,
            now,
            now,
            0
        );
    }

    /**
     * Reconstitute a Provision from persistence.
     */
    public static Provision reconstitute(UUID id, String provisionId, String citizenId,
                                         Money currentBalance, Money totalCredits,
                                         Instant createdAt, Instant updatedAt, int version) {
        return new Provision(id, provisionId, citizenId, currentBalance, totalCredits,
                            createdAt, updatedAt, version);
    }

    /**
     * Credit the provision with an inflow (TaxCredit, Prepayment, or unallocated Payment).
     * Increases both currentBalance and totalCredits.
     * 
     * @param amount Amount to credit (must be positive)
     * @throws DomainException if amount is invalid
     */
    public void credit(Money amount) {
        validateCreditAmount(amount);
        
        this.currentBalance = this.currentBalance.add(amount);
        this.totalCredits = this.totalCredits.add(amount);
        this.updatedAt = Instant.now();
    }

    /**
     * Allocate provision to a debt.
     * Decreases currentBalance but does NOT change totalCredits.
     * 
     * Business Rule RG-002: Provision balance ≥ 0
     * Business Rule RG-008: Allocation ≤ available amount
     * 
     * @param amount Amount to allocate (must be positive and ≤ currentBalance)
     * @throws DomainException if business rules are violated
     */
    public void allocate(Money amount) {
        validateAllocationAmount(amount);
        
        // RG-008: Allocation ≤ available amount
        if (amount.isGreaterThan(currentBalance)) {
            throw new DomainException(
                String.format("Cannot allocate %s %s: insufficient provision balance %s %s (RG-008)",
                    amount.getAmount(), amount.getCurrencyCode(),
                    currentBalance.getAmount(), currentBalance.getCurrencyCode())
            );
        }

        Money newBalance = this.currentBalance.subtract(amount);
        
        // RG-002: Provision balance ≥ 0
        if (newBalance.isNegative()) {
            throw new DomainException(
                String.format("Allocation would result in negative balance: %s %s (RG-002)",
                    newBalance.getAmount(), newBalance.getCurrencyCode())
            );
        }

        this.currentBalance = newBalance;
        this.updatedAt = Instant.now();
    }

    /**
     * Check if provision has sufficient balance for an allocation.
     * 
     * @param amount Amount to check
     * @return true if provision has sufficient balance
     */
    public boolean hasSufficientBalance(Money amount) {
        if (amount == null) {
            return false;
        }
        return currentBalance.isGreaterThanOrEqual(amount);
    }

    /**
     * Get the available balance for allocation.
     * 
     * @return Current balance available
     */
    public Money getAvailableBalance() {
        return currentBalance;
    }

    private static void validateProvisionId(String provisionId) {
        if (provisionId == null || provisionId.isBlank()) {
            throw new DomainException("Provision ID cannot be null or blank");
        }
    }

    private static void validateCitizenId(String citizenId) {
        if (citizenId == null || citizenId.isBlank()) {
            throw new DomainException("Citizen ID cannot be null or blank");
        }
    }

    private void validateCreditAmount(Money amount) {
        if (amount == null) {
            throw new DomainException("Credit amount cannot be null");
        }
        if (!amount.isPositive()) {
            throw new DomainException("Credit amount must be positive");
        }
        if (!amount.getCurrencyCode().equals(currentBalance.getCurrencyCode())) {
            throw new DomainException(
                String.format("Credit currency %s does not match provision currency %s",
                    amount.getCurrencyCode(), currentBalance.getCurrencyCode())
            );
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
                String.format("Allocation currency %s does not match provision currency %s",
                    amount.getCurrencyCode(), currentBalance.getCurrencyCode())
            );
        }
    }

    public int getVersion() {
        return version;
    }
}

// Made with Bob
