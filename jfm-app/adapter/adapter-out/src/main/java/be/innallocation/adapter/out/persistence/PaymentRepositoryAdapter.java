package be.innallocation.adapter.out.persistence;

import be.innallocation.adapter.out.persistence.mapper.PaymentEntityMapper;
import be.innallocation.domain.model.Payment;
import be.innallocation.domain.port.out.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing PaymentRepository port using Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final SpringDataPaymentRepository springDataRepository;
    private final PaymentEntityMapper mapper;

    @Override
    public Payment save(Payment payment) {
        var entity = mapper.toEntity(payment);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return springDataRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Payment> findByPaymentId(String paymentId) {
        return springDataRepository.findByPaymentId(paymentId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Payment> findByBankReference(String bankReference) {
        return springDataRepository.findByBankReference(bankReference)
            .map(mapper::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return springDataRepository.findAllOrderByCreatedAtDesc()
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByPaymentId(String paymentId) {
        return springDataRepository.existsByPaymentId(paymentId);
    }

    @Override
    public boolean existsByBankReference(String bankReference) {
        return springDataRepository.existsByBankReference(bankReference);
    }
}

// Made with Bob
