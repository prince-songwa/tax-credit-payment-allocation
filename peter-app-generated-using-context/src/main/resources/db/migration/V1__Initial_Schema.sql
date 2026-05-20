-- Tax Credit Payment Allocation System - Initial Schema
-- Hexagonal Architecture with Domain-Driven Design

-- Tax Credits Table
CREATE TABLE tax_credits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    citizen_id VARCHAR(50) NOT NULL UNIQUE,
    current_balance DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    total_prepayments DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    total_allocations DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(10) NOT NULL DEFAULT 'EUR',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_tax_credit_balance CHECK (current_balance >= 0),
    CONSTRAINT chk_tax_credit_prepayments CHECK (total_prepayments >= 0),
    CONSTRAINT chk_tax_credit_allocations CHECK (total_allocations >= 0)
);

CREATE INDEX idx_tax_credits_citizen ON tax_credits(citizen_id);
CREATE INDEX idx_tax_credits_status ON tax_credits(status);

-- Prepayments Table
CREATE TABLE prepayments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_reference VARCHAR(100) NOT NULL UNIQUE,
    idempotency_key VARCHAR(100) NOT NULL UNIQUE,
    citizen_id VARCHAR(50) NOT NULL,
    tax_credit_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'EUR',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    CONSTRAINT chk_prepayment_amount CHECK (amount >= 0.01 AND amount <= 999999.99),
    CONSTRAINT fk_prepayment_tax_credit FOREIGN KEY (tax_credit_id) REFERENCES tax_credits(id)
);

CREATE INDEX idx_prepayments_citizen ON prepayments(citizen_id);
CREATE INDEX idx_prepayments_status ON prepayments(status);
CREATE INDEX idx_prepayments_tax_credit ON prepayments(tax_credit_id);

-- Debts Table
CREATE TABLE debts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    debt_reference VARCHAR(100) NOT NULL UNIQUE,
    citizen_id VARCHAR(50) NOT NULL,
    debt_type VARCHAR(30) NOT NULL,
    original_amount DECIMAL(12, 2) NOT NULL,
    remaining_balance DECIMAL(12, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'EUR',
    status VARCHAR(20) NOT NULL DEFAULT 'OUTSTANDING',
    due_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    settled_at TIMESTAMP,
    CONSTRAINT chk_debt_original_amount CHECK (original_amount > 0),
    CONSTRAINT chk_debt_remaining_balance CHECK (remaining_balance >= 0)
);

CREATE INDEX idx_debts_citizen ON debts(citizen_id);
CREATE INDEX idx_debts_status ON debts(status);
CREATE INDEX idx_debts_due_date ON debts(due_date);
CREATE INDEX idx_debts_type ON debts(debt_type);

-- Payments Table
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bank_reference VARCHAR(100) NOT NULL UNIQUE,
    structured_reference VARCHAR(50) NOT NULL,
    debt_reference VARCHAR(100) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'EUR',
    payment_date DATE NOT NULL,
    debtor_account VARCHAR(50) NOT NULL,
    debtor_name VARCHAR(200) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RECEIVED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_payment_amount CHECK (amount > 0)
);

CREATE INDEX idx_payments_bank_ref ON payments(bank_reference);
CREATE INDEX idx_payments_structured_ref ON payments(structured_reference);
CREATE INDEX idx_payments_debt_ref ON payments(debt_reference);
CREATE INDEX idx_payments_status ON payments(status);

-- Allocations Table
CREATE TABLE allocations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    allocation_reference VARCHAR(100) NOT NULL UNIQUE,
    source_type VARCHAR(20) NOT NULL,
    source_id BIGINT NOT NULL,
    debt_id BIGINT NOT NULL,
    citizen_id VARCHAR(50) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'EUR',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    applied_at TIMESTAMP,
    CONSTRAINT chk_allocation_amount CHECK (amount > 0),
    CONSTRAINT fk_allocation_debt FOREIGN KEY (debt_id) REFERENCES debts(id)
);

CREATE INDEX idx_allocations_source ON allocations(source_type, source_id);
CREATE INDEX idx_allocations_debt ON allocations(debt_id);
CREATE INDEX idx_allocations_citizen ON allocations(citizen_id);
CREATE INDEX idx_allocations_status ON allocations(status);

-- Accounting Entries Table
CREATE TABLE accounting_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(100) NOT NULL,
    account_code VARCHAR(20) NOT NULL,
    account_name VARCHAR(100) NOT NULL,
    entry_type VARCHAR(10) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'EUR',
    description VARCHAR(500) NOT NULL,
    reference_type VARCHAR(20) NOT NULL,
    reference_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_accounting_amount CHECK (amount > 0),
    CONSTRAINT chk_entry_type CHECK (entry_type IN ('DEBIT', 'CREDIT'))
);

CREATE INDEX idx_accounting_transaction ON accounting_entries(transaction_id);
CREATE INDEX idx_accounting_reference ON accounting_entries(reference_type, reference_id);
CREATE INDEX idx_accounting_account ON accounting_entries(account_code);

-- Made with Bob
