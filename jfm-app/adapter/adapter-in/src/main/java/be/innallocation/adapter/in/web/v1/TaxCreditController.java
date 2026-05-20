package be.innallocation.adapter.in.web.v1;

import be.innallocation.adapter.in.web.v1.dto.CreateTaxCreditRequest;
import be.innallocation.adapter.in.web.v1.dto.TaxCreditResponse;
import be.innallocation.domain.common.Money;
import be.innallocation.domain.port.in.CreateTaxCreditUseCase;
import be.innallocation.domain.port.out.TaxCreditRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for Tax Credit operations.
 * Exposes endpoints for creating and managing tax credits.
 */
@RestController
@RequestMapping("/api/v1/tax-credits")
@RequiredArgsConstructor
public class TaxCreditController {

    private final CreateTaxCreditUseCase createTaxCreditUseCase;
    private final TaxCreditRepository taxCreditRepository;

    /**
     * Get all tax credits.
     * GET /api/v1/tax-credits
     */
    @GetMapping
    public ResponseEntity<List<TaxCreditResponse>> getAllTaxCredits() {
        var taxCredits = taxCreditRepository.findAll();
        
        var response = taxCredits.stream()
            .map(tc -> new TaxCreditResponse(
                tc.getTaxCreditId(),
                tc.getCitizenId(),
                tc.getAmount().getAmount(),
                tc.getAmount().getCurrencyCode(),
                tc.getStatus().name(),
                tc.getCreatedAt()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Create a new tax credit.
     * POST /api/v1/tax-credits
     */
    @PostMapping
    public ResponseEntity<TaxCreditResponse> createTaxCredit(
            @Valid @RequestBody CreateTaxCreditRequest request) {

        var command = new CreateTaxCreditUseCase.CreateTaxCreditCommand(
            request.taxCreditId(),
            request.citizenId(),
            Money.of(request.amount(), request.currency())
        );

        var result = createTaxCreditUseCase.execute(command);

        var response = new TaxCreditResponse(
            result.taxCreditId(),
            result.citizenId(),
            result.amount().getAmount(),
            result.amount().getCurrencyCode(),
            result.status(),
            java.time.Instant.now() // Use current time for creation response
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

// Made with Bob
