🚀 Bank FX System — Spring Boot + DDD + JPA + PDF + FX

A complete banking and foreign-exchange system built with Spring Boot, following the Domain-Driven Design (DDD) architecture.
It includes:

User registration 

Bank account generation

Local + Forex transaction system

Balance validation

FX rate conversion

Automatic transaction logs

PDF statements with:

Logo

Watermark

Table formatting

QR Code

Signature

Pagination

Statement generation (PDF / TEXT)

📁 Project Architecture (DDD)
bank-fx/
│── domain/
│   ├── model/                # Entities & domain models
│   ├── repository/           # Repository interfaces
│
│── application/
│   ├── dto/                  # Request/Response DTOs
│   ├── service/              # Business logic
│   ├── annotation/           # Transaction logs, interceptors
│   ├── exception/            # Custom exceptions
│
│── infrastructure/
│   ├── persistance/jpa/      # JpaRepositories implementations
│   ├── config/               # Beans, password encoder, FX config
│
│── adapters/
│   ├── web/                  # REST controllers
│
│── resources/
│   ├── pdf/                  # Generated PDF statements
│   ├── application.properties
│
└── pom.xml

🛠️ Technologies
Layer	Stack
Backend	Spring Boot 3+, Spring Web, Spring Data JPA
Database	SQLserver / mongodb
PDF	iText PDF 2.1.7
Logging	AOP custom TransactionLog
Build	Maven
Language	Java 17+
⚙️ Installation
1️⃣ Clone the repo
git clone https://github.com/belguiti/transaction_bank.git

📌 Available Endpoints (Complete Guide)
🧑‍💻 1. User Registration
POST /api/users/register
✔ Example JSON
{
  "email": "john@example.com",
  "password": "123456",
  "firstName": "John",
  "lastName": "Doe",
  "currency": "USD",
  "initialDeposit": 500
}

✔ Response

Creates user

Creates account

Returns saved user

🏦 2. Get User by Email
GET /api/users/email/{email}
GET /api/users/email/john@example.com

💳 3. Transfer Money
POST /api/transactions/transfer
✔ JSON request
{
  "fromAccountNumber": "ACC1763991731472",
  "toAccountNumber": "ACC1763825334537",
  "amount": 150
}

💡 Behavior:

If currencies are same → Local transfer

If different → Forex transfer

Checks:

User blocked?

Sufficient balance?

Accounts exist?

✔ Response
{
  "transactionId": "TXN17372819",
  "amount": 150,
  "type": "LOCAL_TRANSFER"
}

📜 4. Get All Transactions for User
GET /api/transactions/{accountNumber}

Example:

GET /api/transactions/ACC1763991731472

📄 5. Generate TEXT Statement
GET /api/statements/{accountNumber}/text

Example:

http://localhost:8080/api/statements/ACC1763991731472/text?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59


Returns plain text:

Bank Account Statement
======================
Account Number: ACC1763991731472
...

📘 6. Generate PDF Statement
GET /api/statements/{accountNumber}/pdf

Example:

http://localhost:8080/api/statements/ACC1763991731472/pdf?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59

📂 Output saved to:
src/main/resources/pdf/statement_ACC1763991731472.pdf

PDF Includes:

✔ Header with logo
✔ Watermark
✔ Table with colors
✔ Signature & stamp
✔ QR Code linking to verification URL
✔ Pagination
✔ Generated date

🧪 How to Test All Endpoints (Postman)
Endpoint	Method	Description
/api/users/register	POST	Create user + bank account
/api/users/email/{email}	GET	Get user by email
/api/transactions/transfer	POST	Make transaction
/api/transactions/{account}	GET	List all transactions
/api/statements/{account}/text	GET	Text statement
/api/statements/{account}/pdf	GET	PDF statement + save
