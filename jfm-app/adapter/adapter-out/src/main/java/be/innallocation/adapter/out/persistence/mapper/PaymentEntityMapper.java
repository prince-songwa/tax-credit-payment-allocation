package be.innallocation.adapter.out.persistence.mapper;

import be.innallocation.adapter.out.persistence.entity.PaymentJpaEntity;
import be.innallocation.domain.common.Money;
import be.innallocation.domain.model.Payment;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

/**
 * Mapper between Payment domain model and PaymentJpaEntity.
 */
@Component
public class PaymentEntityMapper {

    public PaymentJpaEntity toEntity(Payment payment) {
        PaymentJpaEntity entity = new PaymentJpaEntity();
        entity.setId(payment.getId());
        entity.setPaymentId(payment.getPaymentId());
        entity.setBankReference(payment.getBankReference());
        entity.setStructuredReference(payment.getStructuredReference().orElse(null));
        entity.setDebtId(payment.getDebtId().map(UUID::toString).orElse(null));
        entity.setAmount(payment.getAmount().getAmount());
        entity.setCurrency(payment.getAmount().getCurrencyCode());
        entity.setPaymentDate(payment.getPaymentDate());
        entity.setDebtorAccount(payment.getDebtorAccount());
        entity.setDebtorName(payment.getDebtorName());
        entity.setStatus(payment.getStatus().name());
        entity.setCreatedAt(payment.getCreatedAt());
        return entity;
    }

    public Payment toDomain(PaymentJpaEntity entity) {
        Currency currency = Currency.getInstance(entity.getCurrency());
        Money amount = Money.of(entity.getAmount(), currency);
        Optional<String> structuredReference = Optional.ofNullable(entity.getStructuredReference());
        Optional<UUID> debtId = Optional.ofNullable(entity.getDebtId()).map(UUID::fromString);

        return Payment.reconstitute(
            entity.getId(),
            entity.getPaymentId(),
            entity.getBankReference(),
            structuredReference,
            debtId,
            amount,
            entity.getPaymentDate(),
            entity.getDebtorAccount(),
            entity.getDebtorName(),
            Payment.PaymentStatus.valueOf(entity.getStatus()),
            entity.getCreatedAt()
        );
    }
}

// Made with Bob
