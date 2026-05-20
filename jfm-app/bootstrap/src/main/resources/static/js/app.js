// API Base URL
const API_BASE = '/api/v1';

// State management
const state = {
    currentTab: 'dashboard',
    taxCredits: [],
    debts: [],
    payments: []
};

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    initializeTabs();
    initializeForms();
    loadDashboard();
});

// Tab Management
function initializeTabs() {
    const tabButtons = document.querySelectorAll('.tab-button');
    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const tabName = button.dataset.tab;
            switchTab(tabName);
        });
    });
}

function switchTab(tabName) {
    // Update buttons
    document.querySelectorAll('.tab-button').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');

    // Update content
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });
    document.getElementById(tabName).classList.add('active');

    state.currentTab = tabName;

    // Load data for the tab
    switch(tabName) {
        case 'dashboard':
            loadDashboard();
            break;
        case 'tax-credits':
            loadTaxCredits();
            break;
        case 'debts':
            loadDebts();
            break;
        case 'payments':
            loadPayments();
            break;
    }
}

// Form Initialization
function initializeForms() {
    // Tax Credit Form
    document.getElementById('create-tax-credit-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        await createTaxCredit();
    });

    // Debt Form
    document.getElementById('create-debt-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        await createDebt();
    });

    // Payment Form
    document.getElementById('process-payment-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        await processPayment();
    });
}

// Dashboard Functions
async function loadDashboard() {
    try {
        // Load all data in parallel
        const [taxCredits, debts, payments] = await Promise.all([
            fetch(`${API_BASE}/tax-credits`).then(r => r.ok ? r.json() : []).catch(() => []),
            fetch(`${API_BASE}/debts`).then(r => r.ok ? r.json() : []).catch(() => []),
            fetch(`${API_BASE}/payments`).then(r => r.ok ? r.json() : []).catch(() => [])
        ]);

        // Update dashboard stats
        updateDashboardStats(taxCredits, debts, payments);
        
        // Load recent activity
        loadRecentActivity(taxCredits, debts, payments);
    } catch (error) {
        console.error('Error loading dashboard:', error);
        showNotification('Error loading dashboard data', 'error');
    }
}

function updateDashboardStats(taxCredits, debts, payments) {
    // Total credits from tax credits
    const totalCredits = taxCredits.reduce((sum, tc) => sum + (tc.amount || 0), 0);
    
    // Total debt amounts
    const totalDebtAmount = debts.reduce((sum, d) => sum + (d.currentBalance || 0), 0);
    
    // Provision balance (credits - debts for simplified view)
    const provisionBalance = totalCredits - totalDebtAmount;
    document.getElementById('provision-balance').textContent = `€${provisionBalance.toFixed(2)}`;
    
    // Total credits
    document.getElementById('total-credits').textContent = `€${totalCredits.toFixed(2)}`;
    
    // Active debts count
    const activeDebts = debts.filter(d => d.status === 'ACTIVE' || d.status === 'PARTIALLY_PAID').length;
    document.getElementById('active-debts').textContent = activeDebts;
    
    // Recent allocations (count of payments)
    document.getElementById('recent-allocations').textContent = payments.length;
}

function loadRecentActivity(taxCredits, debts, payments) {
    const activityContainer = document.getElementById('recent-activity');
    
    // Combine all activities
    const activities = [];
    
    // Add tax credits
    taxCredits.slice(0, 5).forEach(tc => {
        activities.push({
            type: 'Tax Credit',
            description: `Tax credit created for citizen ${tc.citizenId}`,
            amount: tc.amount,
            date: tc.createdAt
        });
    });
    
    // Add debts
    debts.slice(0, 5).forEach(debt => {
        activities.push({
            type: 'Debt',
            description: `Debt created for citizen ${debt.citizenId}`,
            amount: debt.originalAmount,
            date: debt.createdAt
        });
    });
    
    // Add payments
    payments.slice(0, 5).forEach(payment => {
        activities.push({
            type: 'Payment',
            description: `Payment received from ${payment.debtorName}`,
            amount: payment.amount,
            date: payment.createdAt
        });
    });
    
    // Sort by date (newest first)
    activities.sort((a, b) => new Date(b.date) - new Date(a.date));
    
    // Display top 10
    if (activities.length === 0) {
        activityContainer.innerHTML = '<p class="loading">No recent activity</p>';
        return;
    }
    
    const html = activities.slice(0, 10).map(activity => `
        <div class="activity-item">
            <strong>${activity.type}</strong>: ${activity.description}<br>
            <small>Amount: €${activity.amount.toFixed(2)} | ${formatDate(activity.date)}</small>
        </div>
    `).join('');
    
    activityContainer.innerHTML = html;
}

// Tax Credits Functions
async function loadTaxCredits() {
    try {
        const response = await fetch(`${API_BASE}/tax-credits`);
        if (!response.ok) throw new Error('Failed to load tax credits');
        
        const taxCredits = await response.json();
        state.taxCredits = taxCredits;
        displayTaxCredits(taxCredits);
    } catch (error) {
        console.error('Error loading tax credits:', error);
        document.getElementById('tax-credits-list').innerHTML = 
            '<p class="loading">Error loading tax credits. Make sure the API is running.</p>';
    }
}

function displayTaxCredits(taxCredits) {
    const container = document.getElementById('tax-credits-list');
    
    if (taxCredits.length === 0) {
        container.innerHTML = '<p class="loading">No tax credits found</p>';
        return;
    }

    const table = `
        <table class="table">
            <thead>
                <tr>
                    <th>Tax Credit ID</th>
                    <th>Citizen ID</th>
                    <th>Amount</th>
                    <th>Status</th>
                    <th>Created</th>
                </tr>
            </thead>
            <tbody>
                ${taxCredits.map(tc => `
                    <tr>
                        <td>${tc.taxCreditId}</td>
                        <td>${tc.citizenId}</td>
                        <td>€${tc.amount.toFixed(2)}</td>
                        <td><span class="status-badge status-${tc.status.toLowerCase()}">${tc.status}</span></td>
                        <td>${formatDate(tc.createdAt)}</td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    container.innerHTML = table;
}

async function createTaxCredit() {
    const citizenId = document.getElementById('tc-citizen-id').value;
    const amount = parseFloat(document.getElementById('tc-amount').value);

    try {
        // Generate a unique tax credit ID (in production, this might come from backend)
        const taxCreditId = `TC-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
        
        const response = await fetch(`${API_BASE}/tax-credits`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                taxCreditId,
                citizenId,
                amount,
                currency: 'EUR'  // Default currency
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to create tax credit');
        }

        const result = await response.json();
        showNotification(`Tax credit created successfully! ID: ${result.taxCreditId}`, 'success');
        
        // Reset form and reload list
        document.getElementById('create-tax-credit-form').reset();
        loadTaxCredits();
        loadDashboard();
    } catch (error) {
        console.error('Error creating tax credit:', error);
        showNotification(error.message, 'error');
    }
}

// Debts Functions
async function loadDebts() {
    try {
        const response = await fetch(`${API_BASE}/debts`);
        if (!response.ok) throw new Error('Failed to load debts');
        
        const debts = await response.json();
        state.debts = debts;
        displayDebts(debts);
    } catch (error) {
        console.error('Error loading debts:', error);
        document.getElementById('debts-list').innerHTML = 
            '<p class="loading">Error loading debts. Make sure the API is running.</p>';
    }
}

function displayDebts(debts) {
    const container = document.getElementById('debts-list');
    
    if (debts.length === 0) {
        container.innerHTML = '<p class="loading">No debts found</p>';
        return;
    }

    const table = `
        <table class="table">
            <thead>
                <tr>
                    <th>Debt ID</th>
                    <th>Citizen ID</th>
                    <th>Original Amount</th>
                    <th>Current Balance</th>
                    <th>Structured Ref</th>
                    <th>Status</th>
                    <th>Created</th>
                </tr>
            </thead>
            <tbody>
                ${debts.map(debt => `
                    <tr>
                        <td>${debt.debtId}</td>
                        <td>${debt.citizenId}</td>
                        <td>€${debt.originalAmount.toFixed(2)}</td>
                        <td>€${debt.currentBalance.toFixed(2)}</td>
                        <td>${debt.structuredReference || 'N/A'}</td>
                        <td><span class="status-badge status-${debt.status.toLowerCase()}">${debt.status}</span></td>
                        <td>${formatDate(debt.createdAt)}</td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    container.innerHTML = table;
}

async function createDebt() {
    const citizenId = document.getElementById('debt-citizen-id').value;
    const amount = parseFloat(document.getElementById('debt-amount').value);
    const structuredReference = document.getElementById('debt-structured-ref').value.trim();

    try {
        // Generate a unique debt ID (in production, this might come from backend)
        const debtId = `DEBT-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
        
        const payload = {
            debtId,
            citizenId,
            amount,
            currency: 'EUR'  // Default currency
        };

        if (structuredReference) {
            payload.structuredReference = structuredReference;
        }

        const response = await fetch(`${API_BASE}/debts`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to create debt');
        }

        const result = await response.json();
        const message = structuredReference 
            ? `Debt created successfully! ID: ${result.debtId}`
            : `Debt created and auto-allocated from provision! ID: ${result.debtId}`;
        
        showNotification(message, 'success');
        
        // Reset form and reload list
        document.getElementById('create-debt-form').reset();
        loadDebts();
        loadDashboard();
    } catch (error) {
        console.error('Error creating debt:', error);
        showNotification(error.message, 'error');
    }
}

// Payments Functions
async function loadPayments() {
    try {
        // Note: Payment endpoint might not exist yet, so we'll handle gracefully
        const response = await fetch(`${API_BASE}/payments`);
        if (!response.ok) throw new Error('Failed to load payments');
        
        const payments = await response.json();
        state.payments = payments;
        displayPayments(payments);
    } catch (error) {
        console.error('Error loading payments:', error);
        document.getElementById('payments-list').innerHTML = 
            '<p class="loading">Payment endpoint not yet implemented or no payments found.</p>';
    }
}

function displayPayments(payments) {
    const container = document.getElementById('payments-list');
    
    if (payments.length === 0) {
        container.innerHTML = '<p class="loading">No payments found</p>';
        return;
    }

    const table = `
        <table class="table">
            <thead>
                <tr>
                    <th>Payment ID</th>
                    <th>Bank Reference</th>
                    <th>Amount</th>
                    <th>Debtor</th>
                    <th>Status</th>
                    <th>Date</th>
                </tr>
            </thead>
            <tbody>
                ${payments.map(payment => `
                    <tr>
                        <td>${payment.paymentId}</td>
                        <td>${payment.bankReference}</td>
                        <td>€${payment.amount.toFixed(2)}</td>
                        <td>${payment.debtorName}</td>
                        <td><span class="status-badge status-${payment.status.toLowerCase()}">${payment.status}</span></td>
                        <td>${formatDate(payment.paymentDate)}</td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    container.innerHTML = table;
}

async function processPayment() {
    const bankReference = document.getElementById('payment-bank-ref').value;
    const amount = parseFloat(document.getElementById('payment-amount').value);
    const structuredReference = document.getElementById('payment-structured-ref').value.trim();
    const debtorAccount = document.getElementById('payment-debtor-account').value;
    const debtorName = document.getElementById('payment-debtor-name').value;

    try {
        const payload = {
            bankReference,
            amount,
            currency: 'EUR',  // Default currency
            debtorAccount,
            debtorName,
            paymentDate: new Date().toISOString().split('T')[0]
        };

        if (structuredReference) {
            payload.structuredReference = structuredReference;
        }

        const response = await fetch(`${API_BASE}/payments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const error = await response.json();
            const errorMsg = error.message || error.error || 'Failed to process payment';
            throw new Error(errorMsg);
        }

        const result = await response.json();
        
        // Determine success message based on result
        let message = 'Payment processed successfully!';
        if (result.allocatedToDebtId) {
            message = `Payment allocated to debt ${result.allocatedToDebtId}`;
        } else if (result.addedToProvision) {
            message = 'Payment added to provision';
        }
        
        showNotification(message, 'success');
        
        // Reset form and reload list
        document.getElementById('process-payment-form').reset();
        loadPayments();
        loadDashboard();
    } catch (error) {
        console.error('Error processing payment:', error);
        showNotification(error.message, 'error');
    }
}

// Utility Functions
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-GB', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function showNotification(message, type = 'info') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `notification ${type} show`;
    
    setTimeout(() => {
        notification.classList.remove('show');
    }, 5000);
}

// Error handling for fetch
window.addEventListener('unhandledrejection', (event) => {
    console.error('Unhandled promise rejection:', event.reason);
    showNotification('An unexpected error occurred', 'error');
});

// Made with Bob
