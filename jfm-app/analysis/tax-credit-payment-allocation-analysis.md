## ✅ TAX CREDIT & PAYMENT ALLOCATION SYSTEM

# FUNCTIONAL & TECHNICAL ANALYSIS

## Document metadata

*   **Product / System:** `Tax Management Platform`
*   **Program / Release:** `MVP0 — Phase 1`
*   **Author:** `Bob`
*   **Contributors:** `Technical Team`
*   **Status:** `Draft`
*   **Version:** `1.0`
*   **Date:** `2026-05-13`

***

# EPIC `E-0`: `Tax Credit and Payment Management`

## Epic objective

*   **Business problem:** Citizens need a transparent, auditable system to manage prepayments, standalone tax credits, provisions, payments and debts.
*   **Value:**
    *   Automated debt settlement via provision
    *   Full auditability
    *   Reduced manual work

***

# FEATURE `F-0.1`: `Debt Creation and Management`

## Feature objective

*   Creation and lifecycle of debts
*   Automatic allocation from provision if no structured reference

***

# FEATURE `F-0.2`: `Provision Funding`

## Feature objective

*   Prepayments, tax credits, and unallocated payments fund Provision

***

# FEATURE `F-0.3`: `Payment Processing`

## Feature objective

*   Payment allocated if debt found, otherwise goes to Provision

***

# FEATURE `F-0.4`: `Allocation Management`

## Feature objective

*   Allocate Provision (or payment) to debts

***

### 2. Domain Model Overview

#### 2.1 Aggregates

### TaxCredit Aggregate

* Standalone aggregate
* Contains its own amount
* Not linked to other aggregates

---

### Prepayment Aggregate

* Standalone
* Not linked to TaxCredit nor Payment
* Feeds Provision

---

### Provision Aggregate

* Central funding aggregate
* Fed by:
  * TaxCredit
  * Prepayment
  * Unallocated Payment

* Used to settle debts

**Invariant:**

Provision balance = total inflows - allocated amounts
Provision balance ≥ 0

---

### Payment Aggregate

* Allocated OR unallocated
* Unallocated → Provision

---

### Allocation Aggregate

* Links Provision or Payment to Debt

---

### Debt Aggregate

* Contains:
  * structured_reference
  * balance
  * lifecycle state

---

### 4. Data Model

#### 4.1 Core Tables

### tax_credits

- id (UUID, PK)
- tax_credit_id (VARCHAR UNIQUE)
- citizen_id
- amount
- currency
- status
- created_at

---

### prepayments

- id (UUID, PK)
- prepayment_id (UNIQUE)
- citizen_id
- amount
- currency
- payment_reference
- status
- idempotency_key
- created_at

---

### provisions

- id (UUID, PK)
- provision_id (UNIQUE)
- citizen_id (UNIQUE)
- current_balance
- total_credits
- currency
- version
- created_at
- updated_at

✅ **Correction applied:**  
`total_debits` **removed** (not relevant to domain model)

---

### debts ✅ (ADDED / COMPLETED)

- debt_id (PK)
- citizen_id
- structured_reference (VARCHAR(50), UNIQUE)
- original_amount
- current_balance
- currency
- status (PENDING, ACTIVE, PARTIALLY_PAID, SETTLED, CANCELLED)
- version (optimistic locking)
- created_at
- updated_at

---

### payments

- id (UUID, PK)
- payment_id
- bank_reference (UNIQUE)
- structured_reference
- debt_id (NULLABLE)
- amount
- currency
- payment_date
- debtor_account
- debtor_name
- status (ALLOCATED | UNALLOCATED)
- created_at

---

### allocations

- id (UUID, PK)
- allocation_id (UNIQUE)
- source_type (PROVISION | PAYMENT)
- source_id
- debt_id
- amount
- currency
- status
- allocation_date
- applied_date
- created_at

---

### accounting_entries

- id (UUID, PK)
- entry_id
- transaction_id
- transaction_type
- source_reference
- account_code
- entry_type (DEBIT | CREDIT)
- amount
- entry_date
- created_at

---

### 5. Business Rules

- RG-001: Prepayment > 0
- RG-002: Provision balance ≥ 0
- RG-003: Idempotency enforced
- RG-004: Structured reference format required if present
- RG-005: Oldest debt first
- RG-006: Accounting balanced
- RG-008: Allocation ≤ available amount
- RG-009: Payment allocated once
- RG-011: Debt without reference uses provision

✅ **Correction applied:**  
Removed dependency on `total_debits`

---

### 6. Allocation Rules Engine

#### Algorithm


WHEN provision updated OR inflow received:

Sort debts
Allocate provision

WHEN debt created WITHOUT structured_reference:

Allocate directly from provision


---

### 7. Accounting Integration

#### Key change

Provision is the liability holder

---

### Entries

**Provision Funding**

DEBIT Bank
CREDIT Provision

**Allocation**

DEBIT Provision
CREDIT Receivable

---

### 9. Edge Cases

- Payment without debt → goes to provision ✅
- Debt without reference → consumes provision ✅

---

### 10. Tests

Added:

- Debt without reference auto-allocation ✅
- Provision-driven flows ✅

---

## Summary

System now based on:

1. Independent aggregates:
   - TaxCredit
   - Prepayment
2. Central aggregate:
   - Provision ✅
3. Core aggregates:
   - Debt ✅ (added properly)
   - Payment
   - Allocation

✅ Key correction:
- Provision is now the **true financial pivot**
- No artificial link between TaxCredit / Prepayment / Payment
- Debt properly modeled with structured_reference