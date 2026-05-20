package be.innallocation.adapter.out.persistence;

import be.innallocation.adapter.out.persistence.mapper.TaxCreditEntityMapper;
import be.innallocation.domain.model.TaxCredit;
import be.innallocation.domain.port.out.TaxCreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing TaxCreditRepository port using Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class TaxCreditRepositoryAdapter implements TaxCreditRepository {

    private final SpringDataTaxCreditRepository springDataRepository;
    private final TaxCreditEntityMapper mapper;

    @Override
    public TaxCredit save(TaxCredit taxCredit) {
        var entity = mapper.toEntity(taxCredit);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<TaxCredit> findById(UUID id) {
        return springDataRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<TaxCredit> findByTaxCreditId(String taxCreditId) {
        return springDataRepository.findByTaxCreditId(taxCreditId)
            .map(mapper::toDomain);
    }

    @Override
    public List<TaxCredit> findAll() {
        return springDataRepository.findAllOrderByCreatedAtDesc()
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByTaxCreditId(String taxCreditId) {
        return springDataRepository.existsByTaxCreditId(taxCreditId);
    }
}

// Made with Bob
