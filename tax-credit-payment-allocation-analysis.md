***

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
*   **Related references:**
    *   Backlog / Epic: `<link to backlog>`
    *   Mockups: `<link to UI designs>`
    *   OpenAPI / Postman: `<link to API specs>`
    *   ADR (decisions): `<link to architecture decisions>`
    *   Runbook / Ops: `<link to operations guide>`

***

## Document structure

This document is organized according to the hierarchy:

*   **EPIC** → **FEATURE** → **USER STORY** → **TECHNICAL ANALYSIS**

**Recommended numbering rules:**

*   EPIC: `E-<n>` (e.g., `E-0`)
*   FEATURE: `F-<epic>.<n>` (e.g., `F-0.1`)
*   USER STORY: `US-<epic>.<feature>.<n>` (e.g., `US-0.1.1`)
*   Business rules: `RG-<NNN>`
*   Edge cases: `EC-<NNN>`
*   Patterns/regex: `RGX-<NNN>`
*   UI errors: `ERR_<SCOPE>_<NAME>`
*   API errors: `API_<DOMAIN>_<NAME>`
*   Tests: `UT_<...>`, `IT_<...>`, `PERF_<...>`, `SEC_<...>`, `A11Y_<...>`

***

# EPIC `E-0`: `Tax Credit and Payment Management`

## Epic objective

*   **Business problem:** Citizens need a transparent, auditable system to manage prepayments (VAT and advance payments), apply tax credits to debts, make payments via bank transfer, and track all financial operations with proper accounting entries.
*   **Value:**
    *   Automated debt settlement through tax credit allocation
    *   Real-time payment processing and reconciliation
    *   Complete audit trail for all financial operations
    *   Reduced manual intervention and errors
    *   Improved citizen experience with clear debt status
*   **In scope:**
    *   Debt creation and lifecycle management
    *   Prepayment processing (VAT and advance payments)
    *   Tax credit management and allocation
    *   Payment processing via bank transfer
    *   Accounting entry generation
*   **Out of scope:**
    *   Tax calculation and assessment
    *   Payment gateway integration (handled separately)
    *   Refund processing
    *   Multi-currency support
*   **Major constraints:** 
    *   Must comply with financial regulations and audit requirements
    *   All operations must be idempotent
    *   Accounting entries must be generated synchronously
    *   System must handle concurrent operations safely
    *   Data retention requirements: 10 years minimum

***

# FEATURE `F-0.1`: `Debt Creation and Management`

## Feature objective

*   **Goal:** Enable the system to create, track, and manage tax debts throughout their lifecycle, from creation to settlement.
*   **Inputs / Triggers:**
    *   Tax assessment completed
    *   Administrative fee imposed
    *   Penalty applied
    *   Manual debt creation by authorized personnel
*   **Outputs:**
    *   Debt record created with unique identifier
    *   Debt status tracked (PENDING, ACTIVE, PARTIALLY_PAID, SETTLED, CANCELLED)
    *   Debt balance calculated and maintained
    *   Notifications sent to citizen
*   **Dependencies:**
    *   Tax assessment system
    *   Citizen management system
    *   Notification service
    *   Accounting/Ledger service

***

# FEATURE `F-0.2`: `Prepayment and Tax Credit Management`

## Feature objective

*   **Goal:** Enable citizens to make prepayments (VAT and advance payments) that feed into a tax credit balance, which can then be automatically allocated to outstanding debts.
*   **Inputs / Triggers:** 
    *   Citizen initiates prepayment (VAT or advance payment)
    *   System receives payment confirmation
    *   Automatic allocation rules trigger
*   **Outputs:** 
    *   Tax credit balance updated
    *   Allocations created and applied to debts
    *   Accounting entries generated
    *   Notifications sent to citizen
*   **Dependencies:** 
    *   Payment processing system
    *   Debt management system
    *   Accounting/Ledger service
    *   Notification service

***

# FEATURE `F-0.3`: `Payment via Bank Transfer`

## Feature objective

*   **Goal:** Enable citizens to settle debts by bank transfer using a structured reference that identifies the specific debt.
*   **Inputs / Triggers:** 
    *   Citizen initiates bank transfer with structured reference
    *   Bank processes transfer and notifies system
    *   System receives payment notification
*   **Outputs:** 
    *   Payment recorded in system
    *   Allocation created linking payment to debt
    *   Debt balance updated
    *   Accounting entries generated
    *   Confirmation sent to citizen
*   **Dependencies:** 
    *   Banking integration service
    *   Debt management system
    *   Allocation service
    *   Accounting/Ledger service

***

# FEATURE `F-0.4`: `Allocation Management`

## Feature objective

*   **Goal:** Manage the allocation of tax credits and payments to debts, ensuring proper tracking, application, and auditability.
*   **Inputs / Triggers:** 
    *   Tax credit available for allocation
    *   Payment received for specific debt
    *   Automatic allocation rules triggered
    *   Manual allocation request (if applicable)
*   **Outputs:** 
    *   Allocation records created
    *   Debt balances updated
    *   Tax credit balance reduced (if applicable)
    *   Accounting entries generated
*   **Dependencies:** 
    *   Tax credit service
    *   Payment service
    *   Debt management system
    *   Accounting/Ledger service

***

## USER STORY `US-0.1.1`: `Create Tax Debt from Assessment`

### 1. Context and objective

**User Story reference**

*   **ID:** `US-0.1.1`
*   **Title:** `Create Tax Debt from Assessment`
*   **Priority:** `Critical`
*   **As a:** `Tax Assessment System`
*   **I want:** `To create a debt record when a tax assessment is finalized`
*   **So that:** `The citizen's tax obligation is properly tracked and can be settled through payments or tax credits`

**Technical objective**

*   Create Debt aggregate from tax assessment data
*   Generate unique debt identifier with structured reference
*   Set initial debt status and balance
*   Generate accounting entries for the debt
*   Notify citizen of new debt

**Technical prerequisites**

*   Debt aggregate schema and database tables
*   Structured reference generation service
*   Accounting/Ledger service API
*   Notification service API
*   Tax assessment system integration
*   Citizen validation service

**Assumptions & out of scope**

*   Assumptions:
    *   Tax assessment has been validated and approved
    *   Citizen exists in the system
    *   Debt amount is positive and within valid range
*   Out of scope:
    *   Tax calculation logic
    *   Assessment approval workflow
    *   Debt dispute handling

***

## USER STORY `US-0.1.2`: `Update Debt Status and Balance`

### 1. Context and objective

**User Story reference**

*   **ID:** `US-0.1.2`
*   **Title:** `Update Debt Status and Balance`
*   **Priority:** `High`
*   **As a:** `System`
*   **I want:** `To update debt status and balance when allocations are applied`
*   **So that:** `The debt lifecycle is accurately tracked and citizens can see their current obligations`

**Technical objective**

*   Apply allocation to debt balance
*   Update debt status based on remaining balance
*   Track payment history
*   Generate accounting entries for balance changes
*   Publish domain events for status changes

**Technical prerequisites**

*   Debt aggregate with status management
*   Allocation service integration
*   Accounting/Ledger service API
*   Event bus for domain events
*   Transaction management for consistency

**Assumptions & out of scope**

*   Assumptions:
    *   Allocation has been validated
    *   Debt exists and is in payable status
    *   Concurrent updates are handled via optimistic locking
*   Out of scope:
    *   Allocation reversal (handled separately)
    *   Debt cancellation workflow
    *   Interest calculation

***

## USER STORY `US-0.1.3`: `Query Debt Status and History`

### 1. Context and objective

**User Story reference**

*   **ID:** `US-0.1.3`
*   **Title:** `Query Debt Status and History`
*   **Priority:** `Medium`
*   **As a:** `Citizen`
*   **I want:** `To view my current debts and their payment history`
*   **So that:** `I can understand my tax obligations and track payments`

**Technical objective**

*   Provide API to query debts by citizen ID
*   Return debt details including current balance and status
*   Include allocation history for each debt
*   Support filtering by status and date range
*   Ensure proper authorization

**Technical prerequisites**

*   Debt query repository
*   Allocation history tracking
*   API authentication and authorization
*   Response pagination support
*   Performance optimization (indexing)

**Assumptions & out of scope**

*   Assumptions:
    *   Citizen is authenticated
    *   Query performance is acceptable with proper indexing
    *   Data is eventually consistent
*   Out of scope:
    *   Real-time updates (polling acceptable)
    *   Export to PDF/Excel
    *   Historical data beyond retention period

***

## USER STORY `US-0.2.1`: `Citizen Makes VAT Prepayment`

### 1. Context and objective

**User Story reference**

*   **ID:** `US-0.1.1`
*   **Title:** `Citizen Makes VAT Prepayment`
*   **Priority:** `High`
*   **As a:** `Citizen taxpayer`
*   **I want:** `To make a VAT prepayment that feeds into my tax credit`
*   **So that:** `I can reduce my future tax obligations and have funds available for automatic debt settlement`

**Technical objective**

*   Accept and process VAT prepayments from citizens
*   Create or update TaxCredit aggregate with the prepayment amount
*   Generate accounting entries for traceability
*   Trigger automatic allocation rules if applicable debts exist

**Technical prerequisites**

*   TaxCredit aggregate schema and database tables
*   Accounting/Ledger service API
*   Payment validation service
*   Citizen authentication and authorization
*   Allocation rules engine
*   Event bus for domain events

**Assumptions & out of scope**

*   Assumptions: 
    *   Citizen is authenticated and authorized
    *   Payment has been validated by payment gateway
    *   Citizen has a valid tax identifier
*   Out of scope: 
    *   Payment gateway integration details
    *   Tax calculation logic
    *   Refund processing

***

## USER STORY `US-0.3.1`: `Payment via Bank Transfer with Structured Reference`

### 1. Context and objective

**User Story reference**

*   **ID:** `US-0.2.1`
*   **Title:** `Payment via Bank Transfer with Structured Reference`
*   **Priority:** `Critical`
*   **As a:** `Citizen taxpayer`
*   **I want:** `To pay a specific debt via bank transfer using a structured reference`
*   **So that:** `The payment is automatically applied to the correct debt without manual intervention`

**Technical objective**

*   Receive and process bank transfer notifications
*   Parse structured reference to identify target debt
*   Create Payment aggregate with payment details
*   Create Allocation linking payment to identified debt
*   Update debt balance
*   Generate accounting entries

**Technical prerequisites**

*   Banking integration service (CODA/MT940 file processing)
*   Payment aggregate schema and database tables
*   Structured reference parsing logic
*   Debt management service API
*   Allocation service
*   Accounting/Ledger service API

**Assumptions & out of scope**

*   Assumptions:
    *   Bank provides payment notifications in standard format
    *   Structured reference format is standardized
    *   Debt exists and is in payable status
*   Out of scope:
    *   Bank account management
    *   International transfers
    *   Currency conversion

***

## USER STORY `US-0.4.1`: `Automatic Allocation of Tax Credit to Debts`

### 1. Context and objective

**User Story reference**

*   **ID:** `US-0.3.1`
*   **Title:** `Automatic Allocation of Tax Credit to Debts`
*   **Priority:** `High`
*   **As a:** `System`
*   **I want:** `To automatically allocate available tax credit to outstanding debts based on priority rules`
*   **So that:** `Debts are settled efficiently without manual intervention`

**Technical objective**

*   Implement allocation rules engine
*   Query outstanding debts and prioritize them
*   Create Allocation records linking tax credit to debts
*   Update tax credit balance and debt balances
*   Generate accounting entries for each allocation
*   Handle partial allocations when credit is insufficient

**Technical prerequisites**

*   Allocation rules configuration
*   Debt management service API
*   Tax credit service
*   Allocation aggregate schema
*   Accounting/Ledger service API
*   Transaction management for consistency

**Assumptions & out of scope**

*   Assumptions:
    *   Allocation rules are configured and validated
    *   Debts have priority indicators
    *   System can handle concurrent allocations
*   Out of scope:
    *   Manual allocation override (future feature)
    *   Allocation reversal (handled separately)
    *   Cross-citizen allocations

***

### 2. Domain Model Overview

#### 2.1 Aggregates

**TaxCredit Aggregate**

*   **Root Entity:** TaxCredit
*   **Responsibility:** Manage citizen's tax credit balance from prepayments
*   **Key Operations:**
    *   Create tax credit account
    *   Add prepayment to balance
    *   Allocate credit to debt (reduce balance)
    *   Query current balance
*   **Invariants:**
    *   Balance = Total Prepayments - Total Allocations
    *   Balance cannot be negative
    *   All operations must be auditable

**Payment Aggregate**

*   **Root Entity:** Payment
*   **Responsibility:** Represent a payment made via bank transfer
*   **Key Operations:**
    *   Record payment from bank notification
    *   Parse structured reference
    *   Link to target debt
    *   Track payment status
*   **Invariants:**
    *   Payment amount must be positive
    *   Structured reference must be valid
    *   Payment can only be applied once

**Allocation Aggregate**

*   **Root Entity:** Allocation
*   **Responsibility:** Materialize and track assignment of credit/payment to debt
*   **Key Operations:**
    *   Create allocation from tax credit to debt
    *   Create allocation from payment to debt
    *   Apply allocation to debt
    *   Reverse allocation (if needed)
*   **Invariants:**
    *   Allocation amount must not exceed source amount
    *   Allocation must reference valid source and target
    *   Once applied, allocation is immutable

**Debt Aggregate**

*   **Root Entity:** Debt
*   **Responsibility:** Manage debt lifecycle and settlement
*   **Key Operations:**
    *   Create debt
    *   Apply allocation (reduce balance)
    *   Calculate remaining balance
    *   Track payment history
*   **Invariants:**
    *   Debt balance = Original amount - Sum of allocations
    *   Debt cannot be overpaid
    *   Settled debts cannot receive new allocations

#### 2.2 Domain Events

*   `PrepaymentCreated`: Published when prepayment is recorded
*   `TaxCreditUpdated`: Published when tax credit balance changes
*   `PaymentReceived`: Published when bank transfer is processed
*   `AllocationCreated`: Published when allocation is created
*   `AllocationApplied`: Published when allocation is applied to debt
*   `DebtSettled`: Published when debt balance reaches zero
*   `AccountingEntriesGenerated`: Published when accounting entries are created

***

### 3. API Contracts

#### 3.1 Endpoint: `Create Prepayment`

*   **Method & URL:**

```
POST /api/v1/tax-credits/prepayments
```

*   **Required headers**
    *   `Authorization`: `Bearer <token>` — JWT token
    *   `Content-Type`: `application/json`
    *   `X-Idempotency-Key`: `UUID` — Idempotency key

*   **Request body**

```json
{
  "citizenId": "CIT-123456789",
  "prepaymentType": "VAT_PREPAYMENT",
  "amount": 1500.00,
  "currency": "EUR",
  "paymentReference": "PAY-REF-2026-001"
}
```

*   **Response codes**
    *   `201`: Created successfully
    *   `400`: Invalid request
    *   `401`: Unauthorized
    *   `409`: Conflict (duplicate)
    *   `500`: Server error

#### 3.2 Endpoint: `Process Bank Transfer Payment`

*   **Method & URL:**

```
POST /api/v1/payments/bank-transfers
```

*   **Required headers**
    *   `Authorization`: `Bearer <system-token>` — System-to-system auth
    *   `Content-Type`: `application/json`

*   **Request body**

```json
{
  "bankReference": "BANK-2026-123456",
  "structuredReference": "+++123/4567/89012+++",
  "amount": 500.00,
  "currency": "EUR",
  "paymentDate": "2026-05-13",
  "debtorAccount": "BE68539007547034",
  "debtorName": "John Doe"
}
```

*   **Response codes**
    *   `201`: Payment processed
    *   `400`: Invalid structured reference
    *   `404`: Debt not found
    *   `409`: Duplicate payment
    *   `500`: Server error

#### 3.3 Endpoint: `Create Allocation`

*   **Method & URL:**

```
POST /api/v1/allocations
```

*   **Required headers**
    *   `Authorization`: `Bearer <token>`
    *   `Content-Type`: `application/json`

*   **Request body**

```json
{
  "sourceType": "TAX_CREDIT",
  "sourceId": "TC-CIT-123456789",
  "debtId": "DEBT-2026-100",
  "amount": 500.00,
  "currency": "EUR"
}
```

*   **Response codes**
    *   `201`: Allocation created
    *   `400`: Invalid request
    *   `422`: Insufficient balance
    *   `500`: Server error

***

### 4. Data Model

#### 4.1 Core Tables

**tax_credits**
*   `id`: UUID (PK)
*   `citizen_id`: VARCHAR(50) UNIQUE
*   `current_balance`: DECIMAL(12,2)
*   `total_prepayments`: DECIMAL(12,2)
*   `total_allocations`: DECIMAL(12,2)
*   `currency`: VARCHAR(3)
*   `version`: INTEGER (optimistic locking)
*   `created_at`: TIMESTAMP
*   `updated_at`: TIMESTAMP

**prepayments**
*   `id`: UUID (PK)
*   `prepayment_id`: VARCHAR(50) UNIQUE
*   `tax_credit_id`: UUID (FK)
*   `citizen_id`: VARCHAR(50)
*   `prepayment_type`: VARCHAR(20)
*   `amount`: DECIMAL(12,2)
*   `payment_reference`: VARCHAR(50) UNIQUE
*   `status`: VARCHAR(20)
*   `idempotency_key`: UUID UNIQUE
*   `created_at`: TIMESTAMP

**payments**
*   `id`: UUID (PK)
*   `payment_id`: VARCHAR(50) UNIQUE
*   `bank_reference`: VARCHAR(50) UNIQUE
*   `structured_reference`: VARCHAR(50)
*   `debt_id`: VARCHAR(50)
*   `amount`: DECIMAL(12,2)
*   `currency`: VARCHAR(3)
*   `payment_date`: DATE
*   `debtor_account`: VARCHAR(34)
*   `debtor_name`: VARCHAR(100)
*   `status`: VARCHAR(20)
*   `created_at`: TIMESTAMP

**allocations**
*   `id`: UUID (PK)
*   `allocation_id`: VARCHAR(50) UNIQUE
*   `source_type`: VARCHAR(20) (TAX_CREDIT | PAYMENT)
*   `source_id`: UUID
*   `debt_id`: VARCHAR(50)
*   `amount`: DECIMAL(12,2)
*   `currency`: VARCHAR(3)
*   `status`: VARCHAR(20)
*   `allocation_date`: TIMESTAMP
*   `applied_date`: TIMESTAMP
*   `created_at`: TIMESTAMP

**accounting_entries**
*   `id`: UUID (PK)
*   `entry_id`: VARCHAR(50) UNIQUE
*   `transaction_id`: UUID (groups related entries)
*   `transaction_type`: VARCHAR(30)
*   `source_reference`: VARCHAR(50)
*   `account_code`: VARCHAR(20)
*   `entry_type`: VARCHAR(10) (DEBIT | CREDIT)
*   `amount`: DECIMAL(12,2)
*   `fiscal_year`: INTEGER
*   `entry_date`: DATE
*   `created_at`: TIMESTAMP

***

### 5. Business Rules

*   **RG-001:** Prepayment amount must be > 0 and ≤ 999,999.99
*   **RG-002:** Tax credit balance = Total prepayments - Total allocations
*   **RG-003:** Idempotency key ensures duplicate requests return same result
*   **RG-004:** Structured reference format: +++XXX/XXXX/XXXXX+++
*   **RG-005:** Automatic allocation prioritizes oldest debts first
*   **RG-006:** Accounting entries must balance (Debit = Credit)
*   **RG-007:** All amounts in same transaction must use same currency
*   **RG-008:** Allocation amount cannot exceed source balance
*   **RG-009:** Payment can only be allocated once to a debt
*   **RG-010:** Citizens can only access their own tax credits and payments

***

### 6. Allocation Rules Engine

#### 6.1 Allocation Priority Rules

1. **Debt Age**: Oldest debts allocated first
2. **Debt Type Priority**: 
   - Critical debts (e.g., penalties)
   - Standard tax debts
   - Administrative fees
3. **Minimum Allocation**: Allocate at least 10% of debt or full credit if less
4. **Partial Allocation**: If credit insufficient, allocate to highest priority debt

#### 6.2 Allocation Algorithm

```
WHEN prepayment created OR tax credit updated:
  1. Query outstanding debts for citizen
  2. Sort debts by priority rules
  3. FOR EACH debt in sorted list:
     a. Calculate available credit
     b. IF credit > 0:
        - Calculate allocation amount (min of debt balance and credit)
        - Create allocation record
        - Update tax credit balance
        - Update debt balance
        - Generate accounting entries
     c. IF credit = 0: BREAK
  4. Publish AllocationCompleted event
```

***

### 7. Accounting Integration

#### 7.1 Chart of Accounts

*   `1000`: Bank Account (Asset)
*   `2100`: Tax Credit Liability (Liability)
*   `1200`: Accounts Receivable - Tax Debts (Asset)
*   `4000`: Tax Revenue (Revenue)

#### 7.2 Accounting Entries by Transaction Type

**Prepayment (VAT or Advance)**
```
DEBIT:  1000 Bank Account          1500.00
CREDIT: 2100 Tax Credit Liability  1500.00
```

**Payment via Bank Transfer**
```
DEBIT:  1000 Bank Account          500.00
CREDIT: 1200 Accounts Receivable   500.00
```

**Allocation from Tax Credit to Debt**
```
DEBIT:  2100 Tax Credit Liability  500.00
CREDIT: 1200 Accounts Receivable   500.00
```

**Allocation from Payment to Debt**
```
(No additional entry - already recorded in payment)
```

***

### 8. Error Handling

#### 8.1 API Error Codes

*   `API_TAXCREDIT_INVALID_AMOUNT`: Invalid prepayment amount
*   `API_TAXCREDIT_DUPLICATE_PAYMENT`: Duplicate payment reference
*   `API_PAYMENT_INVALID_REFERENCE`: Invalid structured reference format
*   `API_PAYMENT_DEBT_NOT_FOUND`: Debt not found for structured reference
*   `API_ALLOCATION_INSUFFICIENT_BALANCE`: Insufficient credit for allocation
*   `API_ALLOCATION_DEBT_SETTLED`: Cannot allocate to settled debt
*   `API_ACCOUNTING_IMBALANCE`: Accounting entries do not balance

***

### 9. Edge Cases

*   **EC-001:** Concurrent prepayments → Optimistic locking prevents conflicts
*   **EC-002:** Payment for already settled debt → Return error, no allocation
*   **EC-003:** Structured reference for non-existent debt → Return 404
*   **EC-004:** Allocation during debt update → Transaction isolation ensures consistency
*   **EC-005:** Accounting service failure → Rollback entire transaction
*   **EC-006:** Partial allocation when credit < debt → Create partial allocation
*   **EC-007:** Multiple debts with same priority → Use debt creation date as tiebreaker

***

### 10. Technical Acceptance Criteria

#### 10.1 Unit Tests (80% coverage minimum)

*   `UT_PREPAYMENT_001`: Create prepayment → Balance updated
*   `UT_PAYMENT_001`: Parse structured reference → Debt ID extracted
*   `UT_ALLOCATION_001`: Allocate credit → Balance reduced
*   `UT_ACCOUNTING_001`: Generate entries → Debit = Credit
*   `UT_RULES_001`: Apply priority rules → Correct order

#### 10.2 Integration Tests

*   `IT_PREPAYMENT_001`: End-to-end prepayment flow
*   `IT_PAYMENT_001`: Bank transfer to debt settlement
*   `IT_ALLOCATION_001`: Automatic allocation triggered
*   `IT_CONCURRENT_001`: Concurrent operations handled correctly

#### 10.3 Performance

*   API response time (p95): < 500ms
*   Throughput: > 100 req/sec
*   Database query time: < 50ms

#### 10.4 Security

*   Authentication on all endpoints
*   Authorization checks for citizen data
*   Input validation and sanitization
*   Rate limiting: 100 req/min per citizen
*   Audit logging for all operations

***

### 11. Deployment Considerations

*   Database migrations via Flyway
*   Feature flags for gradual rollout
*   Monitoring: Prometheus + Grafana
*   Logging: ELK stack with correlation IDs
*   Alerting: PagerDuty for critical errors
*   Backup strategy: Daily backups, 10-year retention

***

### 12. Future Enhancements

*   Manual allocation override capability
*   Allocation reversal workflow
*   Multi-currency support
*   Refund processing
*   Payment plan integration
*   Mobile app support
*   Real-time notifications via WebSocket

***

## Summary

This technical analysis covers the complete tax credit and payment allocation system, including:

1. **Three main aggregates**: TaxCredit, Payment, and Allocation
2. **Two prepayment types**: VAT prepayments and advance payments
3. **Bank transfer payments**: Using structured references for debt identification
4. **Automatic allocation**: Rules-based allocation of credits to debts
5. **Accounting integration**: Double-entry bookkeeping for all operations
6. **Complete audit trail**: All operations tracked and logged

The system ensures data consistency through optimistic locking, transaction management, and comprehensive validation rules. All financial operations generate accounting entries synchronously to maintain auditability and compliance with regulatory requirements.

***