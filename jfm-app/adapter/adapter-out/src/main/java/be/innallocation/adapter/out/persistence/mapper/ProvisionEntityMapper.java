package be.innallocation.adapter.out.persistence.mapper;

import be.innallocation.adapter.out.persistence.entity.ProvisionJpaEntity;
import be.innallocation.domain.common.Money;
import be.innallocation.domain.model.Provision;
import org.springframework.stereotype.Component;

import java.util.Currency;

/**
 * Mapper between Provision domain model and ProvisionJpaEntity.
 * Manual mapping to maintain clean separation between domain and persistence.
 */
@Component
public class ProvisionEntityMapper {

    public ProvisionJpaEntity toEntity(Provision provision) {
        ProvisionJpaEntity entity = new ProvisionJpaEntity();
        entity.setId(provision.getId());
        entity.setProvisionId(provision.getProvisionId());
        entity.setCitizenId(provision.getCitizenId());
        entity.setCurrentBalance(provision.getCurrentBalance().getAmount());
        entity.setTotalCredits(provision.getTotalCredits().getAmount());
        entity.setCurrency(provision.getCurrentBalance().getCurrencyCode());
        entity.setVersion(provision.getVersion());
        entity.setCreatedAt(provision.getCreatedAt());
        entity.setUpdatedAt(provision.getUpdatedAt());
        return entity;
    }

    public Provision toDomain(ProvisionJpaEntity entity) {
        Currency currency = Currency.getInstance(entity.getCurrency());
        Money currentBalance = Money.of(entity.getCurrentBalance(), currency);
        Money totalCredits = Money.of(entity.getTotalCredits(), currency);

        return Provision.reconstitute(
            entity.getId(),
            entity.getProvisionId(),
            entity.getCitizenId(),
            currentBalance,
            totalCredits,
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getVersion()
        );
    }
}

// Made with Bob
