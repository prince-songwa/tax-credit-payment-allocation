package be.innallocation.domain.model;

import be.innallocation.domain.common.DomainException;
import be.innallocation.domain.common.Money;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Payment Aggregate Root.
 * Represents a payment received from a citizen.
 * 
 * Key characteristics:
 * - Can be ALLOCATED (matched to a debt) or UNALLOCATED
 * - Payment WITH matching debt → allocated directly (RG-009)
 * - Payment WITHOUT matching debt → goes to provision
 * - bank_reference must be UNIQUE to prevent duplicate processing
 * 
 * Business Rules:
 * - RG-009: Payment allocated once
 */
@Getter
@EqualsAndHashCode(of = "id")
public class Payment {

    private final UUID id;
    private final String paymentId;
    private final String bankReference;
    private final Optional<String> structuredReference;
    private final Optional<UUID> debtId;
    private final Money amount;
    private final LocalDate paymentDate;
    private final String debtorAccount;
    private final String debtorName;
    private PaymentStatus status;
    private final Instant createdAt;

    @Getter(AccessLevel.NONE)
    private int version;

    private Payment(UUID id, String paymentId, String bankReference,
                    Optional<String> structuredReference, Optional<UUID> debtId,
                    Money amount, LocalDate paymentDate, String debtorAccount,
                    String debtorName, PaymentStatus status, Instant createdAt) {
        this.id = id;
        this.paymentId = paymentId;
        this.bankReference = bankReference;
        this.structuredReference = structuredReference;
        this.debtId = debtId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.debtorAccount = debtorAccount;
        this.debtorName = debtorName;
        this.status = status;
        this.createdAt = createdAt;
        this.version = 0;
    }

    /**
     * Factory method to create a new unallocated Payment.
     * 
     * @param paymentId Business identifier for the payment
     * @param bankReference Unique bank reference (prevents duplicates)
     * @param structuredReference Optional structured reference for debt matching
     * @param amount Payment amount (must be positive)
     * @param paymentDate Date of payment
     * @param debtorAccount Debtor's account number
     * @param debtorName Debtor's name
     * @return New Payment instance
     * @throws DomainException if business rules are violated
     */
    public static Payment createUnallocated(String paymentId, String bankReference,
                                           Optional<String> structuredReference,
                                           Money amount, LocalDate paymentDate,
                                           String debtorAccount, String debtorName) {
        validatePaymentId(paymentId);
        validateBankReference(bankReference);
        validateAmount(amount);
        validatePaymentDate(paymentDate);
        validateDebtorAccount(debtorAccount);
        validateDebtorName(debtorName);

        return new Payment(
            UUID.randomUUID(),
            paymentId,
            bankReference,
            structuredReference,
            Optional.empty(),
            amount,
            paymentDate,
            debtorAccount,
            debtorName,
            PaymentStatus.UNALLOCATED,
            Instant.now()
        );
    }

    /**
     * Factory method to create a new allocated Payment (matched to a debt).
     * 
     * @param paymentId Business identifier for the payment
     * @param bankReference Unique bank reference (prevents duplicates)
     * @param structuredReference Structured reference that matched the debt
     * @param debtId ID of the matched debt
     * @param amount Payment amount (must be positive)
     * @param paymentDate Date of payment
     * @param debtorAccount Debtor's account number
     * @param debtorName Debtor's name
     * @return New Payment instance
     * @throws DomainException if business rules are violated
     */
    public static Payment createAllocated(String paymentId, String bankReference,
                                         String structuredReference, UUID debtId,
                                         Money amount, LocalDate paymentDate,
                                         String debtorAccount, String debtorName) {
        validatePaymentId(paymentId);
        validateBankReference(bankReference);
        validateAmount(amount);
        validatePaymentDate(paymentDate);
        validateDebtorAccount(debtorAccount);
        validateDebtorName(debtorName);

        if (structuredReference == null || structuredReference.isBlank()) {
            throw new DomainException("Structured reference required for allocated payment");
        }
        if (debtId == null) {
            throw new DomainException("Debt ID required for allocated payment");
        }

        return new Payment(
            UUID.randomUUID(),
            paymentId,
            bankReference,
            Optional.of(structuredReference),
            Optional.of(debtId),
            amount,
            paymentDate,
            debtorAccount,
            debtorName,
            PaymentStatus.ALLOCATED,
            Instant.now()
        );
    }

    /**
     * Reconstitute a Payment from persistence.
     */
    public static Payment reconstitute(UUID id, String paymentId, String bankReference,
                                       Optional<String> structuredReference, Optional<UUID> debtId,
                                       Money amount, LocalDate paymentDate,
                                       String debtorAccount, String debtorName,
                                       PaymentStatus status, Instant createdAt) {
        return new Payment(id, paymentId, bankReference, structuredReference, debtId,
                          amount, paymentDate, debtorAccount, debtorName, status, createdAt);
    }

    /**
     * Mark this payment as allocated to a debt.
     * RG-009: Payment allocated once
     * 
     * @param debtId ID of the debt this payment is allocated to
     * @throws DomainException if payment is already allocated
     */
    public void markAsAllocated(UUID debtId) {
        // RG-009: Payment allocated once
        if (status == PaymentStatus.ALLOCATED) {
            throw new DomainException(
                String.format("Payment %s is already allocated (RG-009)", paymentId)
            );
        }

        if (debtId == null) {
            throw new DomainException("Debt ID cannot be null when allocating payment");
        }

        this.status = PaymentStatus.ALLOCATED;
    }

    /**
     * Check if this payment is allocated to a debt.
     * 
     * @return true if payment is allocated
     */
    public boolean isAllocated() {
        return status == PaymentStatus.ALLOCATED;
    }

    /**
     * Check if this payment is unallocated.
     * 
     * @return true if payment is unallocated
     */
    public boolean isUnallocated() {
        return status == PaymentStatus.UNALLOCATED;
    }

    private static void validatePaymentId(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new DomainException("Payment ID cannot be null or blank");
        }
    }

    private static void validateBankReference(String bankReference) {
        if (bankReference == null || bankReference.isBlank()) {
            throw new DomainException("Bank reference cannot be null or blank");
        }
    }

    private static void validateAmount(Money amount) {
        if (amount == null) {
            throw new DomainException("Payment amount cannot be null");
        }
        if (!amount.isPositive()) {
            throw new DomainException("Payment amount must be positive");
        }
    }

    private static void validatePaymentDate(LocalDate paymentDate) {
        if (paymentDate == null) {
            throw new DomainException("Payment date cannot be null");
        }
    }

    private static void validateDebtorAccount(String debtorAccount) {
        if (debtorAccount == null || debtorAccount.isBlank()) {
            throw new DomainException("Debtor account cannot be null or blank");
        }
    }

    private static void validateDebtorName(String debtorName) {
        if (debtorName == null || debtorName.isBlank()) {
            throw new DomainException("Debtor name cannot be null or blank");
        }
    }

    public enum PaymentStatus {
        ALLOCATED,
        UNALLOCATED
    }
}

// Made with Bob
