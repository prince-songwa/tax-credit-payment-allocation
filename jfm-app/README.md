# Tax Credit & Payment Allocation System

Production-grade Spring Boot application implementing a tax credit and payment management system with hexagonal architecture.

## 🏗️ Architecture

This application follows **Hexagonal Architecture (Ports & Adapters)** principles:

- **Domain**: Pure business logic, no framework dependencies
- **Application**: Use case orchestration
- **Adapters**: 
  - **Adapter-In**: REST API controllers
  - **Adapter-Out**: JPA persistence
- **Bootstrap**: Spring Boot application configuration

## 📋 Features

### Core Aggregates

1. **TaxCredit** - Standalone tax credits that feed provision
2. **Prepayment** - Prepayments with idempotency support
3. **Provision** - Central financial pivot (one per citizen)
4. **Debt** - Debts with optional structured reference
5. **Payment** - Payments that match debts or fund provision
6. **Allocation** - Links between sources and debts

### Business Rules Implemented

- ✅ **RG-001**: Prepayment amount must be positive
- ✅ **RG-002**: Provision balance ≥ 0 (enforced with optimistic locking)
- ✅ **RG-003**: Idempotency enforced for prepayments
- ✅ **RG-004**: Structured reference format validation
- ✅ **RG-005**: Oldest debt first allocation
- ✅ **RG-008**: Allocation ≤ available amount
- ✅ **RG-009**: Payment allocated once
- ✅ **RG-011**: Debt without reference auto-allocates from provision

## 🚀 Quick Start

### Prerequisites

- Java 21
- Maven 3.9+

### Build & Run

```bash
# Step 1: Build the entire project from root directory
mvn clean install

# Step 2: Navigate to bootstrap directory
cd bootstrap

# Step 3: Run the application using Maven
mvn spring-boot:run

# OR run the JAR directly
java -jar target/bootstrap-1.0.0-SNAPSHOT.jar
```

**Important**: Always run `mvn spring-boot:run` from the `bootstrap` directory (where the bootstrap/pom.xml is located), NOT from subdirectories like src/main/java.

The application will start on `http://localhost:8080`

### H2 Console

Access the H2 database console at: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:taxcreditdb`
- Username: `sa`
- Password: (empty)

## 📡 API Endpoints

### 1. Create Tax Credit

Creates a tax credit and automatically credits the citizen's provision.

```bash
POST /api/v1/tax-credits
Content-Type: application/json

{
  "taxCreditId": "TC-2024-001",
  "citizenId": "CITIZEN-123",
  "amount": 1000.00,
  "currency": "EUR"
}
```

**Response:**
```json
{
  "taxCreditId": "TC-2024-001",
  "citizenId": "CITIZEN-123",
  "amount": 1000.00,
  "currency": "EUR",
  "status": "APPLIED"
}
```

### 2. Create Prepayment

Creates a prepayment with idempotency support.

```bash
POST /api/v1/prepayments
Content-Type: application/json

{
  "prepaymentId": "PP-2024-001",
  "citizenId": "CITIZEN-123",
  "amount": 500.00,
  "currency": "EUR",
  "paymentReference": "BANK-REF-12345",
  "idempotencyKey": "unique-key-12345"
}
```

### 3. Create Debt (with structured reference)

Creates a debt with a structured reference for payment matching.

```bash
POST /api/v1/debts
Content-Type: application/json

{
  "debtId": "DEBT-2024-001",
  "citizenId": "CITIZEN-123",
  "structuredReference": "+++123/4567/89012+++",
  "amount": 750.00,
  "currency": "EUR"
}
```

**Response:**
```json
{
  "debtId": "DEBT-2024-001",
  "citizenId": "CITIZEN-123",
  "structuredReference": "+++123/4567/89012+++",
  "originalAmount": 750.00,
  "currentBalance": 750.00,
  "currency": "EUR",
  "status": "ACTIVE",
  "autoAllocated": false
}
```

### 4. Create Debt (without structured reference - auto-allocation)

Creates a debt without structured reference. **Automatically allocates from provision** (RG-011).

```bash
POST /api/v1/debts
Content-Type: application/json

{
  "debtId": "DEBT-2024-002",
  "citizenId": "CITIZEN-123",
  "amount": 300.00,
  "currency": "EUR"
}
```

**Response:**
```json
{
  "debtId": "DEBT-2024-002",
  "citizenId": "CITIZEN-123",
  "structuredReference": null,
  "originalAmount": 300.00,
  "currentBalance": 0.00,
  "currency": "EUR",
  "status": "SETTLED",
  "autoAllocated": true
}
```

### 5. Process Payment

Processes a payment. If structured reference matches a debt, allocates directly. Otherwise, adds to provision.

```bash
POST /api/v1/payments
Content-Type: application/json

{
  "paymentId": "PAY-2024-001",
  "bankReference": "BANK-REF-67890",
  "structuredReference": "+++123/4567/89012+++",
  "amount": 750.00,
  "currency": "EUR",
  "paymentDate": "2024-05-20",
  "debtorAccount": "BE12345678901234",
  "debtorName": "John Doe"
}
```

**Response:**
```json
{
  "paymentId": "PAY-2024-001",
  "bankReference": "BANK-REF-67890",
  "amount": 750.00,
  "currency": "EUR",
  "status": "ALLOCATED",
  "allocatedToDebtId": "DEBT-2024-001",
  "addedToProvision": false
}
```

## 🧪 Testing Scenarios

### Scenario 1: Complete Flow with Auto-Allocation

```bash
# 1. Create tax credit (credits provision)
POST /api/v1/tax-credits
{
  "taxCreditId": "TC-001",
  "citizenId": "CIT-001",
  "amount": 1000.00,
  "currency": "EUR"
}

# 2. Create debt without reference (auto-allocates from provision)
POST /api/v1/debts
{
  "debtId": "DEBT-001",
  "citizenId": "CIT-001",
  "amount": 300.00,
  "currency": "EUR"
}
# Result: Debt is SETTLED, provision balance = 700.00

# 3. Create another debt with reference
POST /api/v1/debts
{
  "debtId": "DEBT-002",
  "citizenId": "CIT-001",
  "structuredReference": "+++123/4567/89012+++",
  "amount": 500.00,
  "currency": "EUR"
}
# Result: Debt is ACTIVE, waiting for payment

# 4. Process payment matching the debt
POST /api/v1/payments
{
  "paymentId": "PAY-001",
  "bankReference": "BANK-001",
  "structuredReference": "+++123/4567/89012+++",
  "amount": 500.00,
  "currency": "EUR",
  "paymentDate": "2024-05-20",
  "debtorAccount": "BE12345678901234",
  "debtorName": "John Doe"
}
# Result: Payment allocated to DEBT-002, debt is SETTLED
```

### Scenario 2: Idempotency Check

```bash
# First request
POST /api/v1/prepayments
{
  "prepaymentId": "PP-001",
  "citizenId": "CIT-001",
  "amount": 500.00,
  "currency": "EUR",
  "paymentReference": "REF-001",
  "idempotencyKey": "KEY-12345"
}
# Result: 201 Created

# Duplicate request with same idempotency key
POST /api/v1/prepayments
{
  "prepaymentId": "PP-002",
  "citizenId": "CIT-001",
  "amount": 500.00,
  "currency": "EUR",
  "paymentReference": "REF-002",
  "idempotencyKey": "KEY-12345"
}
# Result: Returns existing prepayment (PP-001)
```

## 📊 Database Schema

Key tables:
- `provisions` - One per citizen, tracks balance
- `tax_credits` - Tax credits
- `prepayments` - Prepayments with idempotency
- `debts` - Debts with optional structured reference
- `payments` - Payments (allocated or unallocated)
- `allocations` - Links sources to debts

## 🔧 Configuration

Edit `bootstrap/src/main/resources/application.yml` to configure:
- Database connection
- Server port
- Logging levels
- JPA settings

## 📝 Project Structure

```
tax-credit-payment-allocation/
├── domain/                 # Pure business logic
│   ├── model/             # Aggregates (TaxCredit, Debt, etc.)
│   ├── port/
│   │   ├── in/           # Inbound ports (use cases)
│   │   └── out/          # Outbound ports (repositories)
│   └── common/           # Value objects (Money, exceptions)
├── application/           # Use case orchestration
│   └── service/          # Service implementations
├── adapter/
│   ├── adapter-in/       # REST controllers, DTOs
│   └── adapter-out/      # JPA entities, repositories
└── bootstrap/            # Spring Boot application
    └── config/           # Dependency injection
```

## 🎯 Key Design Decisions

1. **Aggregate Independence**: TaxCredit, Prepayment, Payment are independent - no direct foreign keys
2. **Provision as Pivot**: All financial flows go through Provision
3. **Optimistic Locking**: Provision and Debt use version fields for concurrency control
4. **Auto-Allocation**: Debts without structured reference automatically consume provision
5. **Idempotency**: Prepayments use idempotency keys to prevent duplicates

## 📚 Further Reading

- [Hexagonal Architecture Guide](architecture/hexagonal-architecture.md)
- [Domain Analysis](analysis/tax-credit-payment-allocation-analysis.md)

## 🤝 Contributing

This is a production-grade implementation following:
- Clean Architecture principles
- Domain-Driven Design
- SOLID principles
- Hexagonal Architecture pattern