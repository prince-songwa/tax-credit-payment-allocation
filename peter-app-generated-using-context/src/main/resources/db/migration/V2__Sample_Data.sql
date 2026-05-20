-- Sample Data for Testing
-- Tax Credit Payment Allocation System

-- Insert sample debts for testing
INSERT INTO debts (debt_reference, citizen_id, debt_type, original_amount, remaining_balance, currency, status, due_date, created_at, updated_at)
VALUES 
    ('DEBT-123-4567-89001', 'CIT-123456789', 'PENALTY', 500.00, 500.00, 'EUR', 'OUTSTANDING', '2026-01-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('DEBT-123-4567-89002', 'CIT-123456789', 'TAX_DEBT', 1000.00, 1000.00, 'EUR', 'OUTSTANDING', '2026-02-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('DEBT-123-4567-89003', 'CIT-987654321', 'ADMINISTRATIVE_FEE', 250.00, 250.00, 'EUR', 'OUTSTANDING', '2026-03-10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Note: Tax credits, prepayments, payments, allocations, and accounting entries
-- will be created dynamically through the API as users interact with the system

-- Made with Bob
