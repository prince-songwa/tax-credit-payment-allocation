package be.innallocation.domain.port.out;

import be.innallocation.domain.model.TaxCredit;

import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for TaxCredit persistence.
 * Following hexagonal architecture naming conventions for repositories.
 */
public interface TaxCreditRepository {

    /**
     * Save a tax credit.
     * 
     * @param taxCredit Tax credit to save
     * @return Saved tax credit
     */
    TaxCredit save(TaxCredit taxCredit);

    /**
     * Find a tax credit by its technical ID.
     * 
     * @param id Technical ID
     * @return Optional containing the tax credit if found
     */
    Optional<TaxCredit> findById(UUID id);

    /**
     * Find a tax credit by its business ID.
     * 
     * @param taxCreditId Business identifier
     * @return Optional containing the tax credit if found
     */
    Optional<TaxCredit> findByTaxCreditId(String taxCreditId);

    /**
     * Find all tax credits ordered by creation date (newest first).
     * Used for listing/display purposes.
     *
     * @return List of all tax credits ordered by creation date descending
     */
    java.util.List<TaxCredit> findAll();

    /**
     * Check if a tax credit exists by its business ID.
     *
     * @param taxCreditId Business identifier
     * @return true if exists
     */
    boolean existsByTaxCreditId(String taxCreditId);
}

// Made with Bob
