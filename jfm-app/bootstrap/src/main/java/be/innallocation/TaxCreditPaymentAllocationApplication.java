package be.innallocation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class.
 * Bootstraps the Tax Credit & Payment Allocation System.
 */
@SpringBootApplication(scanBasePackages = "be.innallocation")
public class TaxCreditPaymentAllocationApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaxCreditPaymentAllocationApplication.class, args);
    }
}

// Made with Bob
