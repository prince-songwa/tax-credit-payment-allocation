# Tax Credit Payment Allocation System

A comprehensive tax credit payment allocation system built with **Hexagonal Architecture** (Ports and Adapters) using Java 21, Spring Boot 3, and H2 database.

## 🏗️ Architecture

This application implements **Hexagonal Architecture** with clear separation of concerns:

```
├── domain/                 # Core Business Logic (Domain Layer)
│   ├── model/             # Domain Entities & Value Objects
│   ├── port/              # Port Interfaces (Repository contracts)
│   └── service/           # Domain Services
├── application/           # Application Layer
│   └── usecase/          # Use Cases (Application Services)
└── adapter/              # Infrastructure Layer
    ├── persistence/      # Database Adapters (JPA implementations)
    └── rest/            # REST API Adapters (Controllers & DTOs)
```

### Key Benefits
- **Domain Independence**: Business logic isolated from infrastructure
- **Testability**: Easy to test with mock implementations
- **Flexibility**: Easy to swap infrastructure components
- **Maintainability**: Clear boundaries and responsibilities

## 🎯 Features

### 1. Prepayment Management
- Create VAT prepayments and advance payments
- Automatic tax credit balance updates
- Idempotency support (RG-003)
- Amount validation (RG-001: 0.01 - 999,999.99 EUR)

### 2. Bank Transfer Processing
- Process payments with structured references
- Structured reference format validation (RG-004: +++XXX/XXXX/XXXXX+++)
- Automatic debt identification
- Payment allocation to debts

### 3. Automatic Allocation
- Priority-based debt allocation (RG-005)
- Oldest debts first
- Debt type priority: PENALTY > TAX_DEBT > ADMINISTRATIVE_FEE
- Partial allocation support

### 4. Accounting Integration
- Double-entry bookkeeping (RG-006)
- Automatic accounting entries
- Complete audit trail

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven 3.8+

### Running the Application

1. **Clone the repository**
```bash
git clone <repository-url>
cd fodfin-agpr
```

2. **Build the application**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

4. **Access the application**
- Frontend UI: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:taxcreditdb`
  - Username: `sa`
  - Password: (leave empty)

## 📊 API Endpoints

### Create Prepayment
```http
POST /api/tax-credits/prepayments
Content-Type: application/json

{
  "citizenId": "CIT-123456789",
  "type": "VAT_PREPAYMENT",
  "amount": 1500.00,
  "idempotencyKey": "unique-key-123"
}
```

### Process Bank Transfer
```http
POST /api/tax-credits/payments
Content-Type: application/json

{
  "bankReference": "BANK-REF-12345",
  "structuredReference": "+++123/4567/89001+++",
  "amount": 500.00,
  "paymentDate": "2026-05-20",
  "debtorAccount": "BE12 3456 7890 1234",
  "debtorName": "John Doe"
}
```

### Query Tax Credit Balance
```http
GET /api/tax-credits/{citizenId}
```

## 🎯 Business Rules

- **RG-001**: Prepayment amount: 0.01 - 999,999.99 EUR
- **RG-002**: Balance = Total prepayments - Total allocations
- **RG-003**: Idempotency key ensures duplicate prevention
- **RG-004**: Structured reference format: `+++XXX/XXXX/XXXXX+++`
- **RG-005**: Automatic allocation prioritizes oldest debts
- **RG-006**: Accounting entries must balance (Debit = Credit)
- **RG-007**: Currency consistency across related entities
- **RG-008**: Allocation cannot exceed source balance
- **RG-009**: Unique bank reference, payment applied only once
- **RG-010**: Citizens can only access their own data

## 🗄️ Database Schema

### Core Tables
- **tax_credits**: Tax credit balances per citizen
- **prepayments**: Prepayment records
- **debts**: Outstanding debts
- **payments**: Bank transfer payments
- **allocations**: Credit/payment to debt allocations
- **accounting_entries**: Double-entry bookkeeping records

## 🧪 Testing

### Manual Testing with Frontend
1. Start the application
2. Open http://localhost:8080
3. Use the UI to:
   - Create prepayments
   - Process bank transfers
   - Check tax credit balances

### Sample Test Data
The application includes sample debts for testing:
- DEBT-123-4567-89001 (PENALTY, €500.00)
- DEBT-123-4567-89002 (TAX_DEBT, €1,000.00)

## 📝 Technology Stack

- **Java 21**
- **Spring Boot 3.2.5**
- **H2 Database** (in-memory)
- **Flyway** (database migrations)
- **Lombok** (reduce boilerplate)
- **Vanilla JavaScript/HTML/CSS** (frontend)

## 🔒 Security Considerations

- Input validation on all endpoints
- Idempotency keys for duplicate prevention
- Optimistic locking for concurrency control
- Structured reference validation
- Amount range validation

## 📚 Context Source

This application was built based on requirements from **context-studio** (context ID: `ctx_cddd3218f6d0`), which provided comprehensive specifications for the tax credit payment allocation system including:
- Entity definitions and relationships
- Business rules and constraints
- Functional and non-functional requirements
- Allocation priority logic
- Accounting integration requirements

## 🤝 Contributing

This is a demonstration project showcasing hexagonal architecture principles and domain-driven design.

## 📄 License

This project is for educational and demonstration purposes.