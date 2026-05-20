package be.innallocation.adapter.out.persistence.mapper;

import be.innallocation.adapter.out.persistence.entity.DebtJpaEntity;
import be.innallocation.domain.common.Money;
import be.innallocation.domain.model.Debt;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.Optional;

/**
 * Mapper between Debt domain model and DebtJpaEntity.
 */
@Component
public class DebtEntityMapper {

    public DebtJpaEntity toEntity(Debt debt) {
        DebtJpaEntity entity = new DebtJpaEntity();
        entity.setId(debt.getId());
        entity.setDebtId(debt.getDebtId());
        entity.setCitizenId(debt.getCitizenId());
        entity.setStructuredReference(debt.getStructuredReference().orElse(null));
        entity.setOriginalAmount(debt.getOriginalAmount().getAmount());
        entity.setCurrentBalance(debt.getCurrentBalance().getAmount());
        entity.setCurrency(debt.getOriginalAmount().getCurrencyCode());
        entity.setStatus(DebtJpaEntity.DebtStatus.valueOf(debt.getStatus().name()));
        entity.setVersion(debt.getVersion());
        entity.setCreatedAt(debt.getCreatedAt());
        entity.setUpdatedAt(debt.getUpdatedAt());
        return entity;
    }

    public Debt toDomain(DebtJpaEntity entity) {
        Currency currency = Currency.getInstance(entity.getCurrency());
        Money originalAmount = Money.of(entity.getOriginalAmount(), currency);
        Money currentBalance = Money.of(entity.getCurrentBalance(), currency);
        Optional<String> structuredReference = Optional.ofNullable(entity.getStructuredReference());

        return Debt.reconstitute(
            entity.getId(),
            entity.getDebtId(),
            entity.getCitizenId(),
            structuredReference,
            originalAmount,
            currentBalance,
            Debt.DebtStatus.valueOf(entity.getStatus().name()),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getVersion()
        );
    }
}

// Made with Bob
