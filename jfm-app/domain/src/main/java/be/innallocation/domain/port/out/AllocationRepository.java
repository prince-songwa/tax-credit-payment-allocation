package be.innallocation.domain.port.out;

import be.innallocation.domain.model.Allocation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for Allocation persistence.
 */
public interface AllocationRepository {

    /**
     * Save an allocation.
     * 
     * @param allocation Allocation to save
     * @return Saved allocation
     */
    Allocation save(Allocation allocation);

    /**
     * Find an allocation by its technical ID.
     * 
     * @param id Technical ID
     * @return Optional containing the allocation if found
     */
    Optional<Allocation> findById(UUID id);

    /**
     * Find an allocation by its business ID.
     * 
     * @param allocationId Business identifier
     * @return Optional containing the allocation if found
     */
    Optional<Allocation> findByAllocationId(String allocationId);

    /**
     * Find all allocations for a debt.
     * 
     * @param debtId Debt identifier
     * @return List of allocations for the debt
     */
    List<Allocation> findByDebtId(UUID debtId);

    /**
     * Find all allocations from a provision.
     * 
     * @param provisionId Provision identifier
     * @return List of allocations from the provision
     */
    List<Allocation> findByProvisionId(UUID provisionId);

    /**
     * Check if an allocation exists by its business ID.
     * 
     * @param allocationId Business identifier
     * @return true if exists
     */
    boolean existsByAllocationId(String allocationId);
}

// Made with Bob
