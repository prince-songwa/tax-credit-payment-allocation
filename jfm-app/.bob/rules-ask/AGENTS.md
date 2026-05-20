# Ask Mode Rules

## Project Status
Greenfield - no code exists. Domain model in `analysis/tax-credit-payment-allocation-analysis.md`.

## Technology Stack
- Java 21
- Spring Boot 3 (latest)
- JPA/Hibernate for persistence
- Hexagonal Architecture (Ports & Adapters)
- TDD approach with unit and integration tests

## Documentation Context (Non-Obvious)

**Domain Model Location**
- Complete functional spec in `analysis/tax-credit-payment-allocation-analysis.md`
- Contains data model, business rules, accounting integration, edge cases

**Counterintuitive Design Decisions**
- Provision aggregate is the financial pivot (not obvious from name)
- TaxCredit/Prepayment/Payment are NOT linked to each other (unusual for financial systems)
- `total_debits` field was removed from provisions table (mentioned in corrections)

**Critical Business Rules**
- RG-011: Debt without structured_reference uses provision automatically
- Payments without matching debt go to provision (not rejected)
- Allocation priority: oldest debt first (FIFO)