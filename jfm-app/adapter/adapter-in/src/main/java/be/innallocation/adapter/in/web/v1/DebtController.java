package be.innallocation.adapter.in.web.v1;

import be.innallocation.adapter.in.web.v1.dto.CreateDebtRequest;
import be.innallocation.adapter.in.web.v1.dto.DebtResponse;
import be.innallocation.domain.common.Money;
import be.innallocation.domain.port.in.CreateDebtUseCase;
import be.innallocation.domain.port.out.DebtRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for Debt operations.
 * Exposes endpoints for creating and managing debts.
 */
@RestController
@RequestMapping("/api/v1/debts")
@RequiredArgsConstructor
public class DebtController {

    private final CreateDebtUseCase createDebtUseCase;
    private final DebtRepository debtRepository;

    /**
     * Get all debts.
     * GET /api/v1/debts
     */
    @GetMapping
    public ResponseEntity<List<DebtResponse>> getAllDebts() {
        var debts = debtRepository.findAll();
        
        var response = debts.stream()
            .map(debt -> new DebtResponse(
                debt.getDebtId(),
                debt.getCitizenId(),
                debt.getStructuredReference().orElse(null),
                debt.getOriginalAmount().getAmount(),
                debt.getCurrentBalance().getAmount(),
                debt.getOriginalAmount().getCurrencyCode(),
                debt.getStatus().name(),
                debt.getStructuredReference().isEmpty(), // auto-allocated if no structured reference
                debt.getCreatedAt()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Create a new debt.
     * POST /api/v1/debts
     *
     * If no structured reference is provided, the debt will automatically
     * allocate from the citizen's provision (RG-011).
     */
    @PostMapping
    public ResponseEntity<DebtResponse> createDebt(
            @Valid @RequestBody CreateDebtRequest request) {

        var command = new CreateDebtUseCase.CreateDebtCommand(
            request.debtId(),
            request.citizenId(),
            Optional.ofNullable(request.structuredReference()),
            Money.of(request.amount(), request.currency())
        );

        var result = createDebtUseCase.execute(command);

        var response = new DebtResponse(
            result.debtId(),
            result.citizenId(),
            result.structuredReference().orElse(null),
            result.originalAmount().getAmount(),
            result.currentBalance().getAmount(),
            result.originalAmount().getCurrencyCode(),
            result.status(),
            result.autoAllocated(),
            java.time.Instant.now() // Use current time for creation response
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

// Made with Bob
