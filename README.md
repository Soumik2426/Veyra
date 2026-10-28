# Veyra

Veyra is a Java and Spring Boot payment gateway prototype. It models a merchant-facing payment API with order and payment persistence, pluggable payment method adapters, payment state transitions, API key management, and a card tokenization vault backed by PostgreSQL.

This is an in-progress portfolio project. The repository demonstrates the domain model and service boundaries for a payment platform; several integrations and security controls are still being built. See [Current scope and next steps](#current-scope-and-next-steps) before treating it as runnable payment infrastructure.

## What this project demonstrates

- Java 25, Spring Boot, Spring MVC, Spring Data JPA, Jakarta Validation, and PostgreSQL.
- A layered structure with controllers, request/response DTOs, services, repositories, entities, and MapStruct mappers.
- A payment domain with orders, payment attempts, statuses, and an explicit state transition table. Valid transitions are recorded in a transition log.
- A Strategy/Adapter design for routing payment requests by method to processor implementations.
- Merchant signup and API key lifecycle endpoints for creating, listing, revoking, and rotating keys.
- Card tokenization with per-card data encryption keys (DEKs), AES-GCM encryption, encrypted DEKs, and token references.
- Bean validation for request fields and a centralized REST exception handler for selected domain errors.

## Architecture

```text
HTTP Controllers
      |
      v
Domain Services ---- Payment State Machine / Transition Log
      |                                |
      v                                v
Repositories <-------------------- PostgreSQL
      |
      +---- Payment Gateway Router ---- Method Adapters ---- Processor Router
      |
      +---- Card Token Service ---- Encrypted Card Vault
```

The code is organized by feature under `src/main/java/com/payment/veyra`:

| Package | Responsibility |
| --- | --- |
| `merchant` | Merchant signup and API key management |
| `payments` | Orders, payment attempts, state transitions, adapters, and processor strategies |
| `vault` | Card tokenization, encrypted card storage, and token lookup |
| `operations` | Persistence entities for webhook, settlement, and dead-letter concepts; workflows are not implemented yet |
| `common` | Shared money type, enums, validation, exceptions, and utilities |
| `mapper` | MapStruct mappings between entities and API DTOs |

## Technology stack

- Java 25
- Spring Boot 4.0.7
- Spring MVC and Spring Data JPA
- PostgreSQL
- MapStruct and Lombok
- Maven Wrapper

## Getting started

### Prerequisites

- JDK 25
- PostgreSQL

Create a PostgreSQL database and user for the application. The default connection points to `jdbc:postgresql://localhost:5432/veyra_db` with username `veyra`; override these settings for your environment. Hibernate is configured to update the schema at startup for local development.

Set the database and vault encryption settings in your shell before starting the app. `VAULT_MASTER_KEY` must be Base64-encoded key material that decodes to a valid AES key length (16, 24, or 32 bytes). Keep it private and use a fresh key in any environment you control.

PowerShell:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/veyra_db"
$env:DB_USERNAME = "veyra"
$env:DB_PASSWORD = "your-local-database-password"
$env:VAULT_MASTER_KEY = "<base64-encoded-aes-key>"
.\mvnw.cmd spring-boot:run
```

On macOS/Linux, use the same environment variables and run:

```bash
./mvnw spring-boot:run
```

The API listens on `http://localhost:8080` with the default Spring Boot configuration.

## API surface

Implemented controller routes currently include:

| Method | Route | Purpose |
| --- | --- | --- |
| `POST` | `/v1/auth/signup` | Create a merchant and initial owner account |
| `POST` | `/v1/merchant/{merchantId}/api-keys` | Create an API key |
| `GET` | `/v1/merchant/{merchantId}/api-keys` | List a merchant's API keys |
| `DELETE` | `/v1/merchant/{merchantId}/api-keys/{apiKeyId}` | Revoke an API key |
| `POST` | `/v1/merchant/{merchantId}/api-keys/{apiKeyId}/rotate` | Rotate an API key |
| `POST` | `/v1/vault/tokenize` | Validate and tokenize card details |
| `POST` | `/v1/orders` | Create an order |
| `POST` | `/v1/payments/initiate` | Start a payment attempt for an order |
| `POST` | `/v1/payments/{paymentId}/capture` | Request payment capture |

Example order request:

```json
{
  "amount": {
    "amountUnits": 12500,
    "currency": "INR"
  },
  "receipt": "order-1001",
  "notes": {
    "source": "portfolio-demo"
  }
}
```

`amountUnits` is the integer amount in the smallest currency unit (for INR, paise). `expiresAt` is optional; the service assigns a 30-minute default when it is omitted.

Example payment initiation request:

```json
{
  "orderId": "<order-uuid>",
  "method": "UPI",
  "methodDetails": {
    "VPA": "customer@bank"
  }
}
```

Payment method values are `CARD`, `UPI`, `NETBANKING`, and `WALLET`. Processor behavior is simulated and incomplete; these routes do not connect to real payment networks.

## Current scope and next steps

The codebase is intentionally unfinished. Important gaps visible in the current implementation:

- Order, payment, and vault controllers currently use a fixed merchant ID. The merchant context and request authentication flow are not wired in.
- Spring Security is a dependency, but authentication and authorization are not configured. Merchant passwords and API key secrets are not yet safely hashed.
- Payment adapters and processors are scaffolding for simulated routing. Wallet processing, card capture, and asynchronous bank callback handling are incomplete.
- Refund, settlement, webhook delivery, Kafka/transactional outbox, Redis idempotency, and resilience policies are not implemented. Some related persistence entities or enums exist only as domain groundwork.
- The screenshot's 10,000 TPS target, PCI DSS alignment, JMeter results, and guarantees around PAN handling are goals, not validated properties of this codebase. No load test or compliance assessment is included.
- The test suite currently contains only a Spring application context smoke test; payment, security, encryption, and integration behavior need focused tests.

These are natural next steps: complete authentication and merchant scoping, finish the payment simulator and supported adapters, add idempotency and event delivery, then build integration and load tests before making performance or compliance claims.

## Project status

Veyra is a learning and portfolio project under active development. It is suitable for reviewing the current Java/Spring design and implementation, but it is not ready to process real payments or cardholder data.
