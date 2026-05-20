package be.innallocation.domain.model;

import be.innallocation.domain.common.DomainException;
import be.innallocation.domain.common.Money;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Prepayment Aggregate Root.
 * Standalone aggregate representing a prepayment made by a citizen.
 * Not linked to TaxCredit or Payment - feeds Provision independently.
 * 
 * Business Rule RG-001: Prepayment amount must be > 0
 * Business Rule RG-003: Idempotency enforced via idempotency_key
 */
@Getter
@EqualsAndHashCode(of = "id")
public class Prepayment {

    private final UUID id;
    private final String prepaymentId;
    private final String citizenId;
    private final Money amount;
    private final String paymentReference;
    private final String idempotencyKey;
    private PrepaymentStatus status;
    private final Instant createdAt;

    @Getter(AccessLevel.NONE)
    private int version;

    private Prepayment(UUID id, String prepaymentId, String citizenId, Money amount,
                       String paymentReference, String idempotencyKey, 
                       PrepaymentStatus status, Instant createdAt) {
        this.id = id;
        this.prepaymentId = prepaymentId;
        this.citizenId = citizenId;
        this.amount = amount;
        this.paymentReference = paymentReference;
        this.idempotencyKey = idempotencyKey;
        this.status = status;
        this.createdAt = createdAt;
        this.version = 0;
    }

    /**
     * Factory method to create a new Prepayment.
     * 
     * @param prepaymentId Business identifier for the prepayment
     * @param citizenId Citizen identifier
     * @param amount Prepayment amount (must be positive - RG-001)
     * @param paymentReference Payment reference from bank
     * @param idempotencyKey Unique key to prevent duplicate processing (RG-003)
     * @return New Prepayment instance
     * @throws DomainException if business rules are violated
     */
    public static Prepayment create(String prepaymentId, String citizenId, Money amount,
                                    String paymentReference, String idempotencyKey) {
        validatePrepaymentId(prepaymentId);
        validateCitizenId(citizenId);
        validateAmount(amount); // RG-001
        validatePaymentReference(paymentReference);
        validateIdempotencyKey(idempotencyKey); // RG-003

        return new Prepayment(
            UUID.randomUUID(),
            prepaymentId,
            citizenId,
            amount,
            paymentReference,
            idempotencyKey,
            PrepaymentStatus.PENDING,
            Instant.now()
        );
    }

    /**
     * Reconstitute a Prepayment from persistence.
     */
    public static Prepayment reconstitute(UUID id, String prepaymentId, String citizenId,
                                          Money amount, String paymentReference, 
                                          String idempotencyKey, PrepaymentStatus status, 
                                          Instant createdAt) {
        return new Prepayment(id, prepaymentId, citizenId, amount, paymentReference,
                             idempotencyKey, status, createdAt);
    }

    /**
     * Mark this prepayment as applied to provision.
     */
    public void markAsApplied() {
        if (status != PrepaymentStatus.PENDING) {
            throw new DomainException(
                String.format("Cannot apply prepayment %s in status %s", prepaymentId, status)
            );
        }
        this.status = PrepaymentStatus.APPLIED;
    }

    /**
     * Cancel this prepayment.
     */
    public void cancel() {
        if (status == PrepaymentStatus.APPLIED) {
            throw new DomainException(
                String.format("Cannot cancel prepayment %s that has been applied", prepaymentId)
            );
        }
        this.status = PrepaymentStatus.CANCELLED;
    }

    private static void validatePrepaymentId(String prepaymentId) {
        if (prepaymentId == null || prepaymentId.isBlank()) {
            throw new DomainException("Prepayment ID cannot be null or blank");
        }
    }

    private static void validateCitizenId(String citizenId) {
        if (citizenId == null || citizenId.isBlank()) {
            throw new DomainException("Citizen ID cannot be null or blank");
        }
    }

    private static void validateAmount(Money amount) {
        if (amount == null) {
            throw new DomainException("Prepayment amount cannot be null");
        }
        // RG-001: Prepayment > 0
        if (!amount.isPositive()) {
            throw new DomainException("Prepayment amount must be positive (RG-001)");
        }
    }

    private static void validatePaymentReference(String paymentReference) {
        if (paymentReference == null || paymentReference.isBlank()) {
            throw new DomainException("Payment reference cannot be null or blank");
        }
    }

    private static void validateIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new DomainException("Idempotency key cannot be null or blank (RG-003)");
        }
    }

    public enum PrepaymentStatus {
        PENDING,
        APPLIED,
        CANCELLED
    }
}

// Made with Bob
