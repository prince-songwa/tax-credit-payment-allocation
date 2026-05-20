package be.innallocation.adapter.in.web.v1;

import be.innallocation.adapter.in.web.v1.dto.PaymentListResponse;
import be.innallocation.adapter.in.web.v1.dto.PaymentResponse;
import be.innallocation.adapter.in.web.v1.dto.ProcessPaymentRequest;
import be.innallocation.domain.common.Money;
import be.innallocation.domain.port.in.ProcessPaymentUseCase;
import be.innallocation.domain.port.out.PaymentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for Payment operations.
 * Exposes endpoints for processing payments.
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final PaymentRepository paymentRepository;

    /**
     * Get all payments.
     * GET /api/v1/payments
     */
    @GetMapping
    public ResponseEntity<List<PaymentListResponse>> getAllPayments() {
        var payments = paymentRepository.findAll();
        
        var response = payments.stream()
            .map(payment -> new PaymentListResponse(
                payment.getPaymentId(),
                payment.getBankReference(),
                payment.getAmount().getAmount(),
                payment.getAmount().getCurrencyCode(),
                payment.getStatus().name(),
                payment.getPaymentDate(),
                payment.getDebtorName(),
                payment.getDebtorAccount(),
                payment.getCreatedAt()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Process a payment.
     * POST /api/v1/payments
     *
     * If structured reference matches a debt, payment is allocated directly.
     * Otherwise, payment goes to provision.
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody ProcessPaymentRequest request) {

        // Generate unique payment ID
        String paymentId = "PAY-" + UUID.randomUUID().toString();

        var command = new ProcessPaymentUseCase.ProcessPaymentCommand(
            paymentId,
            request.bankReference(),
            Optional.ofNullable(request.structuredReference()),
            Money.of(request.amount(), request.currency()),
            LocalDate.parse(request.paymentDate()),
            request.debtorAccount(),
            request.debtorName()
        );

        var result = processPaymentUseCase.execute(command);

        var response = new PaymentResponse(
            result.paymentId(),
            result.bankReference(),
            result.amount().getAmount(),
            result.amount().getCurrencyCode(),
            result.status(),
            result.allocatedToDebtId().orElse(null),
            result.addedToProvision()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

// Made with Bob