package be.innallocation.adapter.out.persistence.mapper;

import be.innallocation.adapter.out.persistence.entity.PrepaymentJpaEntity;
import be.innallocation.domain.common.Money;
import be.innallocation.domain.model.Prepayment;
import org.springframework.stereotype.Component;

import java.util.Currency;

/**
 * Mapper between Prepayment domain model and PrepaymentJpaEntity.
 */
@Component
public class PrepaymentEntityMapper {

    public PrepaymentJpaEntity toEntity(Prepayment prepayment) {
        PrepaymentJpaEntity entity = new PrepaymentJpaEntity();
        entity.setId(prepayment.getId());
        entity.setPrepaymentId(prepayment.getPrepaymentId());
        entity.setCitizenId(prepayment.getCitizenId());
        entity.setAmount(prepayment.getAmount().getAmount());
        entity.setCurrency(prepayment.getAmount().getCurrencyCode());
        entity.setPaymentReference(prepayment.getPaymentReference());
        entity.setStatus(prepayment.getStatus().name());
        entity.setIdempotencyKey(prepayment.getIdempotencyKey());
        entity.setCreatedAt(prepayment.getCreatedAt());
        return entity;
    }

    public Prepayment toDomain(PrepaymentJpaEntity entity) {
        Currency currency = Currency.getInstance(entity.getCurrency());
        Money amount = Money.of(entity.getAmount(), currency);

        return Prepayment.reconstitute(
            entity.getId(),
            entity.getPrepaymentId(),
            entity.getCitizenId(),
            amount,
            entity.getPaymentReference(),
            entity.getIdempotencyKey(),
            Prepayment.PrepaymentStatus.valueOf(entity.getStatus()),
            entity.getCreatedAt()
        );
    }
}

// Made with Bob
