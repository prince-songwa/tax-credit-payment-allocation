package be.innallocation.domain.port.out;

import be.innallocation.domain.model.Debt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for Debt persistence.
 */
public interface DebtRepository {

    /**
     * Save a debt.
     * Uses optimistic locking via version field.
     * 
     * @param debt Debt to save
     * @return Saved debt
     */
    Debt save(Debt debt);

    /**
     * Find a debt by its technical ID.
     * 
     * @param id Technical ID
     * @return Optional containing the debt if found
     */
    Optional<Debt> findById(UUID id);

    /**
     * Find a debt by its business ID.
     * 
     * @param debtId Business identifier
     * @return Optional containing the debt if found
     */
    Optional<Debt> findByDebtId(String debtId);

    /**
     * Find a debt by its structured reference.
     * Used for payment matching.
     * 
     * @param structuredReference Structured reference
     * @return Optional containing the debt if found
     */
    Optional<Debt> findByStructuredReference(String structuredReference);

    /**
     * Find all active debts for a citizen, ordered by creation date (oldest first).
     * Used for allocation (RG-005: Oldest debt first).
     * 
     * @param citizenId Citizen identifier
     * @return List of active debts ordered by creation date
     */
    List<Debt> findActiveByCitizenIdOrderByCreatedAt(String citizenId);

    /**
     * Find all debts eligible for allocation for a citizen.
     * Includes ACTIVE and PARTIALLY_PAID debts, ordered by creation date.
     * 
     * @param citizenId Citizen identifier
     * @return List of eligible debts ordered by creation date
     */
    List<Debt> findEligibleForAllocationByCitizenId(String citizenId);

    /**
     * Find all debts ordered by creation date (newest first).
     * Used for listing/display purposes.
     *
     * @return List of all debts ordered by creation date descending
     */
    List<Debt> findAll();

    /**
     * Check if a debt exists by its business ID.
     *
     * @param debtId Business identifier
     * @return true if exists
     */
    boolean existsByDebtId(String debtId);

    /**
     * Check if a debt exists by its structured reference.
     * 
     * @param structuredReference Structured reference
     * @return true if exists
     */
    boolean existsByStructuredReference(String structuredReference);
}

// Made with Bob
