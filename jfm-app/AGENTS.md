# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Project Status
Greenfield project - no code exists yet. Domain model defined in `analysis/tax-credit-payment-allocation-analysis.md`.

## Technology Stack
- Java 21
- Spring Boot 3 (latest)
- JPA/Hibernate for persistence
- Hexagonal Architecture (Ports & Adapters)
- TDD approach with unit and integration tests

## Critical Domain Rules (When Implementing)

**Provision Aggregate (Non-Obvious)**
- Provision balance calculation: Do NOT use `total_debits` field (was removed from spec)
- Formula: `current_balance = total_credits - allocated_amounts`
- Must enforce balance ≥ 0 at transaction level with optimistic locking

**Aggregate Independence (Critical)**
- TaxCredit, Prepayment, Payment are independent - do NOT link them directly
- All flows go through Provision aggregate (the financial pivot)

**Auto-Allocation Logic (Non-Standard)**
- Debts WITHOUT `structured_reference` automatically consume provision (not manual)
- Payments WITHOUT matching debt go to provision (not rejected/error)

**Accounting (Non-Obvious)**
- Provision is the liability holder (not individual aggregates)
- Provision funding: DEBIT Bank, CREDIT Provision
- Allocation: DEBIT Provision, CREDIT Receivable