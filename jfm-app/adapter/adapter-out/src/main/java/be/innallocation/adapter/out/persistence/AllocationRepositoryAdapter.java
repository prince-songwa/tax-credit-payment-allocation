package be.innallocation.adapter.out.persistence;

import be.innallocation.adapter.out.persistence.mapper.AllocationEntityMapper;
import be.innallocation.domain.model.Allocation;
import be.innallocation.domain.port.out.AllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing AllocationRepository port using Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class AllocationRepositoryAdapter implements AllocationRepository {

    private final SpringDataAllocationRepository springDataRepository;
    private final AllocationEntityMapper mapper;

    @Override
    public Allocation save(Allocation allocation) {
        var entity = mapper.toEntity(allocation);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Allocation> findById(UUID id) {
        return springDataRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Allocation> findByAllocationId(String allocationId) {
        return springDataRepository.findByAllocationId(allocationId)
            .map(mapper::toDomain);
    }

    @Override
    public List<Allocation> findByDebtId(UUID debtId) {
        return springDataRepository.findByDebtId(debtId)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Allocation> findByProvisionId(UUID provisionId) {
        return springDataRepository.findBySourceId(provisionId)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByAllocationId(String allocationId) {
        return springDataRepository.existsByAllocationId(allocationId);
    }
}

// Made with Bob