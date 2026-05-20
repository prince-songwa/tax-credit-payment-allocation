package be.innallocation.adapter.out.persistence.mapper;

import be.innallocation.adapter.out.persistence.entity.TaxCreditJpaEntity;
import be.innallocation.domain.common.Money;
import be.innallocation.domain.model.TaxCredit;
import org.springframework.stereotype.Component;

import java.util.Currency;

/**
 * Mapper between TaxCredit domain model and TaxCreditJpaEntity.
 */
@Component
public class TaxCreditEntityMapper {

    public TaxCreditJpaEntity toEntity(TaxCredit taxCredit) {
        TaxCreditJpaEntity entity = new TaxCreditJpaEntity();
        entity.setId(taxCredit.getId());
        entity.setTaxCreditId(taxCredit.getTaxCreditId());
        entity.setCitizenId(taxCredit.getCitizenId());
        entity.setAmount(taxCredit.getAmount().getAmount());
        entity.setCurrency(taxCredit.getAmount().getCurrencyCode());
        entity.setStatus(taxCredit.getStatus().name());
        entity.setCreatedAt(taxCredit.getCreatedAt());
        return entity;
    }

    public TaxCredit toDomain(TaxCreditJpaEntity entity) {
        Currency currency = Currency.getInstance(entity.getCurrency());
        Money amount = Money.of(entity.getAmount(), currency);

        return TaxCredit.reconstitute(
            entity.getId(),
            entity.getTaxCreditId(),
            entity.getCitizenId(),
            amount,
            TaxCredit.TaxCreditStatus.valueOf(entity.getStatus()),
            entity.getCreatedAt()
        );
    }
}

// Made with Bob
