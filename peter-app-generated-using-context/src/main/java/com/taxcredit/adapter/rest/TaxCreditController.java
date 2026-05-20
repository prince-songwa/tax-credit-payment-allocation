package com.taxcredit.adapter.rest;

import com.taxcredit.adapter.rest.dto.CreatePrepaymentRequest;
import com.taxcredit.adapter.rest.dto.ProcessBankTransferRequest;
import com.taxcredit.application.usecase.CreatePrepaymentUseCase;
import com.taxcredit.application.usecase.ProcessBankTransferUseCase;
import com.taxcredit.application.usecase.QueryTaxCreditBalanceUseCase;
import com.taxcredit.domain.model.Payment;
import com.taxcredit.domain.model.Prepayment;
import com.taxcredit.domain.model.TaxCredit;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Tax Credit operations
 * Hexagonal Architecture - Adapter layer (REST API)
 */
@RestController
@RequestMapping("/api/tax-credits")
@CrossOrigin(origins = "*")
public class TaxCreditController {
    
    private final CreatePrepaymentUseCase createPrepaymentUseCase;
    private final ProcessBankTransferUseCase processBankTransferUseCase;
    private final QueryTaxCreditBalanceUseCase queryTaxCreditBalanceUseCase;
    
    public TaxCreditController(CreatePrepaymentUseCase createPrepaymentUseCase,
                              ProcessBankTransferUseCase processBankTransferUseCase,
                              QueryTaxCreditBalanceUseCase queryTaxCreditBalanceUseCase) {
        this.createPrepaymentUseCase = createPrepaymentUseCase;
        this.processBankTransferUseCase = processBankTransferUseCase;
        this.queryTaxCreditBalanceUseCase = queryTaxCreditBalanceUseCase;
    }
    
    /**
     * Create a prepayment
     * POST /api/tax-credits/prepayments
     */
    @PostMapping("/prepayments")
    public ResponseEntity<Prepayment> createPrepayment(@Valid @RequestBody CreatePrepaymentRequest request) {
        Prepayment prepayment = createPrepaymentUseCase.execute(
            request.citizenId(),
            request.type(),
            request.amount(),
            request.idempotencyKey()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(prepayment);
    }
    
    /**
     * Process bank transfer payment
     * POST /api/tax-credits/payments
     */
    @PostMapping("/payments")
    public ResponseEntity<Payment> processBankTransfer(@Valid @RequestBody ProcessBankTransferRequest request) {
        Payment payment = processBankTransferUseCase.execute(
            request.bankReference(),
            request.structuredReference(),
            request.amount(),
            request.paymentDate(),
            request.debtorAccount(),
            request.debtorName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }
    
    /**
     * Query tax credit balance
     * GET /api/tax-credits/{citizenId}
     */
    @GetMapping("/{citizenId}")
    public ResponseEntity<TaxCredit> getTaxCreditBalance(@PathVariable String citizenId) {
        TaxCredit taxCredit = queryTaxCreditBalanceUseCase.execute(citizenId);
        return ResponseEntity.ok(taxCredit);
    }
    
    /**
     * Exception handler for validation errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("An error occurred: " + ex.getMessage()));
    }
    
    record ErrorResponse(String message) {}
}

// Made with Bob
