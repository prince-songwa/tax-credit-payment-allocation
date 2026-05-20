// API Base URL
const API_BASE = 'http://localhost:8080/api/tax-credits';

// Tab switching
function showTab(tabName) {
    // Hide all tabs
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelectorAll('.tab-button').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Show selected tab
    document.getElementById(`${tabName}-tab`).classList.add('active');
    event.target.classList.add('active');
}

// Generate UUID for idempotency key
function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0;
        const v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

// Show result
function showResult(elementId, success, message, data = null) {
    const resultDiv = document.getElementById(elementId);
    resultDiv.className = `result ${success ? 'success' : 'error'}`;
    
    let html = `<h3>${success ? '✓ Success' : '✗ Error'}</h3>`;
    html += `<p>${message}</p>`;
    
    if (data) {
        html += `<pre>${JSON.stringify(data, null, 2)}</pre>`;
    }
    
    resultDiv.innerHTML = html;
}

// Prepayment Form
document.getElementById('prepayment-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const data = {
        citizenId: document.getElementById('prep-citizen-id').value,
        type: document.getElementById('prep-type').value,
        amount: parseFloat(document.getElementById('prep-amount').value),
        idempotencyKey: generateUUID()
    };
    
    try {
        const response = await fetch(`${API_BASE}/prepayments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            showResult('prepayment-result', true, 
                `Prepayment created successfully! Reference: ${result.paymentReference}`, result);
            e.target.reset();
        } else {
            showResult('prepayment-result', false, result.message || 'Failed to create prepayment');
        }
    } catch (error) {
        showResult('prepayment-result', false, `Error: ${error.message}`);
    }
});

// Payment Form
document.getElementById('payment-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const data = {
        bankReference: document.getElementById('bank-ref').value,
        structuredReference: document.getElementById('structured-ref').value,
        amount: parseFloat(document.getElementById('pay-amount').value),
        paymentDate: document.getElementById('payment-date').value,
        debtorAccount: document.getElementById('debtor-account').value,
        debtorName: document.getElementById('debtor-name').value
    };
    
    try {
        const response = await fetch(`${API_BASE}/payments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            showResult('payment-result', true, 
                `Payment processed successfully! Bank Reference: ${result.bankReference}`, result);
            e.target.reset();
        } else {
            showResult('payment-result', false, result.message || 'Failed to process payment');
        }
    } catch (error) {
        showResult('payment-result', false, `Error: ${error.message}`);
    }
});

// Balance Form
document.getElementById('balance-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const citizenId = document.getElementById('balance-citizen-id').value;
    
    try {
        const response = await fetch(`${API_BASE}/${citizenId}`);
        
        if (response.ok) {
            const result = await response.json();
            showResult('balance-result', true, 
                `Tax Credit Balance for ${citizenId}`, result);
        } else {
            const error = await response.json();
            showResult('balance-result', false, error.message || 'Failed to fetch balance');
        }
    } catch (error) {
        showResult('balance-result', false, `Error: ${error.message}`);
    }
});

// Set today's date as default for payment date
document.getElementById('payment-date').valueAsDate = new Date();

// Made with Bob
