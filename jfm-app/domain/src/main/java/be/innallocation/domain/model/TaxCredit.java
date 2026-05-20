package be.innallocation.domain.model;

import be.innallocation.domain.common.DomainException;
import be.innallocation.domain.common.Money;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * TaxCredit Aggregate Root.
 * Standalone aggregate representing a tax credit granted to a citizen.
 * Not linked to other aggregates - feeds Provision independently.
 */
@Getter
@EqualsAndHashCode(of = "id")
public class TaxCredit {

    private final UUID id;
    private final String taxCreditId;
    private final String citizenId;
    private final Money amount;
    private TaxCreditStatus status;
    private final Instant createdAt;

    @Getter(AccessLevel.NONE)
    private int version;

    private TaxCredit(UUID id, String taxCreditId, String citizenId, Money amount, 
                      TaxCreditStatus status, Instant createdAt) {
        this.id = id;
        this.taxCreditId = taxCreditId;
        this.citizenId = citizenId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.version = 0;
    }

    /**
     * Factory method to create a new TaxCredit.
     * 
     * @param taxCreditId Business identifier for the tax credit
     * @param citizenId Citizen identifier
     * @param amount Tax credit amount (must be positive)
     * @return New TaxCredit instance
     * @throws DomainException if business rules are violated
     */
    public static TaxCredit create(String taxCreditId, String citizenId, Money amount) {
        validateTaxCreditId(taxCreditId);
        validateCitizenId(citizenId);
        validateAmount(amount);

        return new TaxCredit(
            UUID.randomUUID(),
            taxCreditId,
            citizenId,
            amount,
            TaxCreditStatus.ACTIVE,
            Instant.now()
        );
    }

    /**
     * Reconstitute a TaxCredit from persistence.
     */
    public static TaxCredit reconstitute(UUID id, String taxCreditId, String citizenId, 
                                         Money amount, TaxCreditStatus status, Instant createdAt) {
        return new TaxCredit(id, taxCreditId, citizenId, amount, status, createdAt);
    }

    /**
     * Mark this tax credit as applied to provision.
     */
    public void markAsApplied() {
        if (status != TaxCreditStatus.ACTIVE) {
            throw new DomainException(
                String.format("Cannot apply tax credit %s in status %s", taxCreditId, status)
            );
        }
        this.status = TaxCreditStatus.APPLIED;
    }

    /**
     * Cancel this tax credit.
     */
    public void cancel() {
        if (status == TaxCreditStatus.APPLIED) {
            throw new DomainException(
                String.format("Cannot cancel tax credit %s that has been applied", taxCreditId)
            );
        }
        this.status = TaxCreditStatus.CANCELLED;
    }

    private static void validateTaxCreditId(String taxCreditId) {
        if (taxCreditId == null || taxCreditId.isBlank()) {
            throw new DomainException("Tax credit ID cannot be null or blank");
        }
    }

    private static void validateCitizenId(String citizenId) {
        if (citizenId == null || citizenId.isBlank()) {
            throw new DomainException("Citizen ID cannot be null or blank");
        }
    }

    private static void validateAmount(Money amount) {
        if (amount == null) {
            throw new DomainException("Tax credit amount cannot be null");
        }
        if (!amount.isPositive()) {
            throw new DomainException("Tax credit amount must be positive");
        }
    }

    public enum TaxCreditStatus {
        ACTIVE,
        APPLIED,
        CANCELLED
    }
}

// Made with Bob
