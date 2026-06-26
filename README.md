# Trading Signal Tracker

Spring Boot backend for creating trading signals, tracking live prices through Binance public APIs, evaluating signal state, handling expiry, and persisting final outcomes.

## Tech Stack

- Java 17+
- Spring Boot 3
- Spring MVC
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven
- JUnit 5 / Mockito
- springdoc-openapi Swagger UI
- Docker Compose for local PostgreSQL
- GitHub Actions CI

## Setup Instructions

Required tools:

- Java 17 or newer
- Maven
- PostgreSQL, or Docker Desktop if using `docker-compose.yml`
- Git

Clone the repository:

```bash
git clone https://github.com/himanshuraj687/trading-signal-tracker.git
cd trading-signal-tracker
```

Install dependencies and run tests:

```bash
mvn test
```

## Database Setup

### Option 1: Docker Compose

Start PostgreSQL:

```bash
docker compose up -d
```

This creates:

- Database: `trading_signals`
- Username: `postgres`
- Password: `postgres`
- Port: `5432`

### Option 2: Existing Local PostgreSQL

Create a PostgreSQL database, for example:

```sql
CREATE DATABASE trading_signals;
```

If your database name, username, or password is different, pass them as environment variables.

Example for a local database named `zuvomo`:

```bash
DB_URL=jdbc:postgresql://localhost:5432/zuvomo
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

Do not commit real database passwords to the repository.

## How To Run

Run with Maven:

```bash
mvn spring-boot:run
```

The API runs by default at:

```text
http://localhost:8080
```

If port `8080` is already in use, run on another port:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

Then use:

```text
http://localhost:8081
```

## Configuration

Default configuration is in `src/main/resources/application.yml`.

Supported environment variables:

```bash
DB_URL=jdbc:postgresql://localhost:5432/trading_signals
DB_USERNAME=postgres
DB_PASSWORD=postgres
BINANCE_BASE_URL=https://api.binance.com
SIGNAL_EVALUATION_FIXED_DELAY_MS=60000
```

## API Documentation

Swagger UI is available after the application starts:

```text
http://localhost:8080/swagger-ui.html
```

If running on port `8081`:

```text
http://localhost:8081/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## API Endpoints

Create signal:

```http
POST /api/signals
Content-Type: application/json

{
  "symbol": "BTCUSDT",
  "direction": "BUY",
  "entryPrice": 65000,
  "stopLoss": 64000,
  "targetPrice": 67000,
  "entryTime": "2026-06-27T00:00:00Z",
  "expiryTime": "2026-06-28T00:00:00Z"
}
```

Available endpoints:

- `POST /api/signals`
- `GET /api/signals`
- `GET /api/signals/{id}`
- `GET /api/signals/{id}/status`
- `DELETE /api/signals/{id}`

Successful create response returns HTTP `201 Created`.

## Business Rules

BUY signals:

- `stopLoss < entryPrice`
- `targetPrice > entryPrice`
- `price >= targetPrice` changes status to `TARGET_HIT`
- `price <= stopLoss` changes status to `STOPLOSS_HIT`

SELL signals:

- `stopLoss > entryPrice`
- `targetPrice < entryPrice`
- `price <= targetPrice` changes status to `TARGET_HIT`
- `price >= stopLoss` changes status to `STOPLOSS_HIT`

Time rules:

- `expiryTime` must be after `entryTime`
- `entryTime` may be up to 24 hours in the past
- If current time is after `expiryTime` and no target or stop loss has been hit, status becomes `EXPIRED`

Final state rules:

- `TARGET_HIT` is final
- `STOPLOSS_HIT` is final
- `EXPIRED` is final
- Final signals are never re-evaluated

ROI:

- BUY: `(currentPrice - entryPrice) / entryPrice * 100`
- SELL: `(entryPrice - currentPrice) / entryPrice * 100`
- Returned/stored with two decimal precision when a signal reaches a final state

## Architecture Explanation

The application follows a layered architecture:

- `controller`: exposes REST APIs and maps HTTP requests/responses
- `dto`: defines request and response payloads
- `entity`: contains JPA entities and enums
- `repository`: provides database access through Spring Data JPA
- `service`: contains validation, status evaluation, ROI calculation, Binance integration, and scheduling
- `exception`: centralizes error handling and structured HTTP error responses
- `config`: application beans such as `Clock` and `RestClient`

Business flow:

1. Client submits a signal through `POST /api/signals`.
2. Jakarta Bean Validation checks required fields and positive prices.
3. `SignalValidationService` applies BUY/SELL price rules and time rules.
4. `TradingSignalService` persists the signal with initial status `OPEN`.
5. On reads and scheduled evaluation, open signals are checked against Binance live price.
6. `SignalStatusEvaluator` applies target, stop-loss, expiry, and final-state rules.
7. `RoiCalculator` computes realized ROI when a signal reaches a final status.
8. JPA persists the updated status and nullable `realized_roi`.

External API integration:

- `BinancePriceClient` calls Binance public endpoint:

```text
https://api.binance.com/api/v3/ticker/price?symbol=BTCUSDT
```

- No API key is required.
- The Binance base URL is configurable with `BINANCE_BASE_URL`.
- Unit tests mock the price client where service behavior depends on external data.

Scalability notes:

- Business rules are isolated in small services, so validation and status logic can evolve independently.
- Final states prevent repeated mutation of completed signals.
- Scheduled evaluation is configurable with `SIGNAL_EVALUATION_FIXED_DELAY_MS`.
- Database constraints protect positive prices and valid time ranges.

## Error Handling

Invalid requests return HTTP `400 Bad Request`.

Examples:

- BUY stop loss greater than or equal to entry price
- BUY target less than or equal to entry price
- SELL stop loss less than or equal to entry price
- SELL target greater than or equal to entry price
- Expiry time before entry time
- Entry time older than 24 hours

Missing signals return HTTP `404 Not Found`.

## Tests

Run:

```bash
mvn test
```

Covered:

- BUY validation
- SELL validation
- Time validation
- TARGET_HIT status logic
- STOPLOSS_HIT status logic
- EXPIRED status logic
- Final-state immutability
- BUY ROI calculation
- SELL ROI calculation
- Service-level evaluation with mocked Binance price client

## Loom Video Guide

Record a video under 5 minutes covering:

1. Project structure: controller, dto, entity, repository, service, exception, config.
2. Signal creation flow from Swagger to database.
3. Validation rules for BUY, SELL, and time fields.
4. Status evaluation flow for target hit, stop-loss hit, and expiry.
5. Binance integration through `BinancePriceClient`.
6. Tests and how to run `mvn test`.

## Submission Checklist

- GitHub repository is available.
- Project builds and runs locally with Java 17+ and Maven.
- PostgreSQL setup is documented.
- Swagger endpoint is documented.
- Architecture and business flow are explained.
- Unit tests cover validation, status logic, ROI, and mocked external dependency behavior.
- Loom video can be recorded using the guide above.
