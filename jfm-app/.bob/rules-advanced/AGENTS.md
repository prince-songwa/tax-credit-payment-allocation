# Advanced Mode Rules

## Project Status
Greenfield - no code exists. Implement based on domain model in `analysis/tax-credit-payment-allocation-analysis.md`.

## Technology Stack
- Java 21
- Spring Boot 3 (latest)
- JPA/Hibernate for persistence
- Hexagonal Architecture (Ports & Adapters)
- TDD approach with unit and integration tests

## Critical Implementation Rules

**Provision Balance (Non-Obvious)**
- Do NOT use `total_debits` field (removed from spec)
- Calculate: `current_balance = total_credits - allocated_amounts`
- Enforce balance ≥ 0 with optimistic locking (version field)

**Aggregate Boundaries (Critical)**
- TaxCredit, Prepayment, Payment are independent - NO direct foreign keys between them
- All relationships flow through Provision aggregate only

**Auto-Allocation (Non-Standard)**
- Debt without `structured_reference` → auto-allocate from provision immediately
- Payment without matching debt → add to provision (not error)

**Database Constraints**
- `citizen_id` UNIQUE on provisions table (one provision per citizen)
- `bank_reference` UNIQUE on payments (prevent duplicate processing)
- `structured_reference` UNIQUE on debts when present