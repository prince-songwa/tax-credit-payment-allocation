package be.innallocation.adapter.out.persistence;

import be.innallocation.adapter.out.persistence.mapper.DebtEntityMapper;
import be.innallocation.domain.model.Debt;
import be.innallocation.domain.port.out.DebtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing DebtRepository port using Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class DebtRepositoryAdapter implements DebtRepository {

    private final SpringDataDebtRepository springDataRepository;
    private final DebtEntityMapper mapper;

    @Override
    public Debt save(Debt debt) {
        var entity = mapper.toEntity(debt);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Debt> findById(UUID id) {
        return springDataRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Debt> findByDebtId(String debtId) {
        return springDataRepository.findByDebtId(debtId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Debt> findByStructuredReference(String structuredReference) {
        return springDataRepository.findByStructuredReference(structuredReference)
            .map(mapper::toDomain);
    }

    @Override
    public List<Debt> findActiveByCitizenIdOrderByCreatedAt(String citizenId) {
        return findEligibleForAllocationByCitizenId(citizenId);
    }

    @Override
    public List<Debt> findEligibleForAllocationByCitizenId(String citizenId) {
        return springDataRepository.findEligibleForAllocationByCitizenId(citizenId)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Debt> findAll() {
        return springDataRepository.findAllOrderByCreatedAtDesc()
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByDebtId(String debtId) {
        return springDataRepository.existsByDebtId(debtId);
    }

    @Override
    public boolean existsByStructuredReference(String structuredReference) {
        return springDataRepository.existsByStructuredReference(structuredReference);
    }
}

// Made with Bob
