package be.innallocation.domain.port.out;

import be.innallocation.domain.model.Provision;

import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for Provision persistence.
 * Provision is the central financial pivot - one per citizen.
 */
public interface ProvisionRepository {

    /**
     * Save a provision.
     * Uses optimistic locking via version field to prevent concurrent balance violations.
     * 
     * @param provision Provision to save
     * @return Saved provision
     */
    Provision save(Provision provision);

    /**
     * Find a provision by its technical ID.
     * 
     * @param id Technical ID
     * @return Optional containing the provision if found
     */
    Optional<Provision> findById(UUID id);

    /**
     * Find a provision by its business ID.
     * 
     * @param provisionId Business identifier
     * @return Optional containing the provision if found
     */
    Optional<Provision> findByProvisionId(String provisionId);

    /**
     * Find a provision by citizen ID.
     * One provision per citizen (UNIQUE constraint).
     * 
     * @param citizenId Citizen identifier
     * @return Optional containing the provision if found
     */
    Optional<Provision> findByCitizenId(String citizenId);

    /**
     * Check if a provision exists for a citizen.
     * 
     * @param citizenId Citizen identifier
     * @return true if exists
     */
    boolean existsByCitizenId(String citizenId);
}

// Made with Bob
