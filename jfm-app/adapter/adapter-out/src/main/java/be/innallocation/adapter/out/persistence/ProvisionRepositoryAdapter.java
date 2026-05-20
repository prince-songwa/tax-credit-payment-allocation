package be.innallocation.adapter.out.persistence;

import be.innallocation.adapter.out.persistence.mapper.ProvisionEntityMapper;
import be.innallocation.domain.model.Provision;
import be.innallocation.domain.port.out.ProvisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing ProvisionRepository port using Spring Data JPA.
 * Translates between domain model and JPA entities.
 */
@Component
@RequiredArgsConstructor
public class ProvisionRepositoryAdapter implements ProvisionRepository {

    private final SpringDataProvisionRepository springDataRepository;
    private final ProvisionEntityMapper mapper;

    @Override
    public Provision save(Provision provision) {
        var entity = mapper.toEntity(provision);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Provision> findById(UUID id) {
        return springDataRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Provision> findByProvisionId(String provisionId) {
        return springDataRepository.findByProvisionId(provisionId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Provision> findByCitizenId(String citizenId) {
        return springDataRepository.findByCitizenId(citizenId)
            .map(mapper::toDomain);
    }

    @Override
    public boolean existsByCitizenId(String citizenId) {
        return springDataRepository.existsByCitizenId(citizenId);
    }
}

// Made with Bob
