package be.innallocation.adapter.out.persistence;

import be.innallocation.adapter.out.persistence.mapper.PrepaymentEntityMapper;
import be.innallocation.domain.model.Prepayment;
import be.innallocation.domain.port.out.PrepaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing PrepaymentRepository port using Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class PrepaymentRepositoryAdapter implements PrepaymentRepository {

    private final SpringDataPrepaymentRepository springDataRepository;
    private final PrepaymentEntityMapper mapper;

    @Override
    public Prepayment save(Prepayment prepayment) {
        var entity = mapper.toEntity(prepayment);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Prepayment> findById(UUID id) {
        return springDataRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Prepayment> findByPrepaymentId(String prepaymentId) {
        return springDataRepository.findByPrepaymentId(prepaymentId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Prepayment> findByIdempotencyKey(String idempotencyKey) {
        return springDataRepository.findByIdempotencyKey(idempotencyKey)
            .map(mapper::toDomain);
    }

    @Override
    public boolean existsByPrepaymentId(String prepaymentId) {
        return springDataRepository.existsByPrepaymentId(prepaymentId);
    }

    @Override
    public boolean existsByIdempotencyKey(String idempotencyKey) {
        return springDataRepository.existsByIdempotencyKey(idempotencyKey);
    }
}

// Made with Bob
