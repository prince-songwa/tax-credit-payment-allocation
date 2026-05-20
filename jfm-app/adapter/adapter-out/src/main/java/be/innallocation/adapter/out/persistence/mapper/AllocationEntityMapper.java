package be.innallocation.adapter.out.persistence.mapper;

import be.innallocation.adapter.out.persistence.entity.AllocationJpaEntity;
import be.innallocation.domain.common.Money;
import be.innallocation.domain.model.Allocation;
import org.springframework.stereotype.Component;

import java.util.Currency;

/**
 * Mapper between Allocation domain model and AllocationJpaEntity.
 */
@Component
public class AllocationEntityMapper {

    public AllocationJpaEntity toEntity(Allocation allocation) {
        AllocationJpaEntity entity = new AllocationJpaEntity();
        entity.setId(allocation.getId());
        entity.setAllocationId(allocation.getAllocationId());
        entity.setSourceType(AllocationJpaEntity.AllocationSourceType.valueOf(allocation.getSourceType().name()));
        entity.setSourceId(allocation.getSourceId());
        entity.setDebtId(allocation.getDebtId());
        entity.setAmount(allocation.getAmount().getAmount());
        entity.setCurrency(allocation.getAmount().getCurrencyCode());
        entity.setStatus(AllocationJpaEntity.AllocationStatus.valueOf(allocation.getStatus().name()));
        entity.setAllocationDate(allocation.getAllocationDate());
        entity.setAppliedDate(allocation.getAppliedDate());
        entity.setVersion(0); // Initialize version for new entities
        entity.setCreatedAt(allocation.getCreatedAt());
        return entity;
    }

    public Allocation toDomain(AllocationJpaEntity entity) {
        Currency currency = Currency.getInstance(entity.getCurrency());
        Money amount = Money.of(entity.getAmount(), currency);

        return Allocation.reconstitute(
            entity.getId(),
            entity.getAllocationId(),
            Allocation.AllocationSourceType.valueOf(entity.getSourceType().name()),
            entity.getSourceId(),
            entity.getDebtId(),
            amount,
            Allocation.AllocationStatus.valueOf(entity.getStatus().name()),
            entity.getAllocationDate(),
            entity.getAppliedDate(),
            entity.getCreatedAt()
        );
    }
}

// Made with Bob