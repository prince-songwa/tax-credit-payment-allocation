package be.innallocation.config;

import be.innallocation.application.service.CreateDebtService;
import be.innallocation.application.service.CreatePrepaymentService;
import be.innallocation.application.service.CreateTaxCreditService;
import be.innallocation.application.service.ProcessPaymentService;
import be.innallocation.domain.port.in.CreateDebtUseCase;
import be.innallocation.domain.port.in.CreatePrepaymentUseCase;
import be.innallocation.domain.port.in.CreateTaxCreditUseCase;
import be.innallocation.domain.port.in.ProcessPaymentUseCase;
import be.innallocation.domain.port.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring configuration for dependency injection.
 * Wires together domain, application, and adapter layers.
 */
@Configuration
@EnableTransactionManagement
public class ApplicationConfiguration {

    @Bean
    public CreateTaxCreditUseCase createTaxCreditUseCase(
            TaxCreditRepository taxCreditRepository,
            ProvisionRepository provisionRepository) {
        return new CreateTaxCreditService(taxCreditRepository, provisionRepository);
    }

    @Bean
    public CreatePrepaymentUseCase createPrepaymentUseCase(
            PrepaymentRepository prepaymentRepository,
            ProvisionRepository provisionRepository) {
        return new CreatePrepaymentService(prepaymentRepository, provisionRepository);
    }

    @Bean
    public CreateDebtUseCase createDebtUseCase(
            DebtRepository debtRepository,
            ProvisionRepository provisionRepository,
            AllocationRepository allocationRepository) {
        return new CreateDebtService(debtRepository, provisionRepository, allocationRepository);
    }

    @Bean
    public ProcessPaymentUseCase processPaymentUseCase(
            PaymentRepository paymentRepository,
            DebtRepository debtRepository,
            ProvisionRepository provisionRepository,
            AllocationRepository allocationRepository) {
        return new ProcessPaymentService(
            paymentRepository,
            debtRepository,
            provisionRepository,
            allocationRepository
        );
    }
}

// Made with Bob
