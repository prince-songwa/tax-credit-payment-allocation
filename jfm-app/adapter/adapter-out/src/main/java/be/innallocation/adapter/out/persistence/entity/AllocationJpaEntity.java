package be.innallocation.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA Entity for Allocation aggregate.
 * Maps to allocations table.
 * Links a source (Provision or Payment) to a Debt.
 */
@Entity
@Table(name = "allocations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllocationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "allocation_id", nullable = false, unique = true)
    private String allocationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 20)
    private AllocationSourceType sourceType;

    @Column(name = "source_id", nullable = false)
    private UUID sourceId;

    @Column(name = "debt_id", nullable = false)
    private UUID debtId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AllocationStatus status;

    @Column(name = "allocation_date", nullable = false)
    private LocalDate allocationDate;

    @Column(name = "applied_date")
    private Instant appliedDate;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public enum AllocationSourceType {
        PROVISION,
        PAYMENT
    }

    public enum AllocationStatus {
        PENDING,
        APPLIED,
        CANCELLED
    }
}

// Made with Bob