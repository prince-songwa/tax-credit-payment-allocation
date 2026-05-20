# Plan Mode Rules

## Project Status
Greenfield - no code exists. Domain model in `analysis/tax-credit-payment-allocation-analysis.md`.

## Technology Stack
- Java 21
- Spring Boot 3 (latest)
- JPA/Hibernate for persistence
- Hexagonal Architecture (Ports & Adapters)
- TDD approach with unit and integration tests

## Architectural Constraints (Non-Obvious)

**Aggregate Independence (Critical)**
- TaxCredit, Prepayment, Payment MUST remain independent aggregates
- NO direct foreign keys between these aggregates
- Provision is the ONLY connection point (financial pivot pattern)

**Provision as Central Hub**
- All financial flows route through Provision aggregate
- Provision holds the liability (not individual aggregates)
- Balance must be maintained with optimistic locking (version field)

**Auto-Allocation Architecture**
- System must auto-allocate debts without structured_reference
- Allocation engine triggers on provision updates AND debt creation
- FIFO allocation strategy (oldest debt first)

**Accounting Integration**
- Double-entry bookkeeping required for all transactions
- Provision funding: DEBIT Bank, CREDIT Provision
- Allocation: DEBIT Provision, CREDIT Receivable
- All entries must balance within same transaction boundary