# Trading Signal Tracker

Spring Boot backend for creating trading signals, tracking them against Binance public prices, and persisting final signal state.

## Tech Stack

- Java 17+
- Spring Boot 3
- Spring MVC
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven
- JUnit 5 / Mockito
- springdoc-openapi Swagger UI

## Run Locally

Start PostgreSQL:

```bash
docker compose up -d
```

Run the application:

```bash
mvn spring-boot:run
```

The API runs at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Configuration

Default database settings are in `src/main/resources/application.yml`.

Override with environment variables when needed:

```bash
DB_URL=jdbc:postgresql://localhost:5432/trading_signals
DB_USERNAME=postgres
DB_PASSWORD=postgres
BINANCE_BASE_URL=https://api.binance.com
```

## API

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
  "entryTime": "2026-06-26T10:00:00Z",
  "expiryTime": "2026-06-27T10:00:00Z"
}
```

Other endpoints:

- `GET /api/signals`
- `GET /api/signals/{id}`
- `GET /api/signals/{id}/status`
- `DELETE /api/signals/{id}`

## Business Rules

BUY signals require:

- `stopLoss < entryPrice`
- `targetPrice > entryPrice`

SELL signals require:

- `stopLoss > entryPrice`
- `targetPrice < entryPrice`

Time rules:

- `expiryTime` must be after `entryTime`
- `entryTime` can be up to 24 hours in the past

Status evaluation:

- BUY: `price >= targetPrice` means `TARGET_HIT`
- BUY: `price <= stopLoss` means `STOPLOSS_HIT`
- SELL: `price <= targetPrice` means `TARGET_HIT`
- SELL: `price >= stopLoss` means `STOPLOSS_HIT`
- If current time is after expiry and no target/stop loss was hit, status becomes `EXPIRED`
- `TARGET_HIT`, `STOPLOSS_HIT`, and `EXPIRED` are final states

ROI:

- BUY: `(currentPrice - entryPrice) / entryPrice * 100`
- SELL: `(entryPrice - currentPrice) / entryPrice * 100`
- Stored with two decimal precision when a signal reaches a final state

## Architecture

The application uses a layered structure:

- `controller`: REST endpoints and HTTP response mapping
- `dto`: request/response payloads
- `entity`: JPA model and enums
- `repository`: Spring Data persistence
- `service`: validation, Binance integration, ROI calculation, and status transitions
- `exception`: global exception handling with structured HTTP errors

The controller delegates to `TradingSignalService`. On reads, open signals are evaluated against the current Binance price and saved by the active transaction if their state changes. Final states are never re-evaluated.

## Tests

Run:

```bash
mvn test
```

Covered:

- BUY validation
- SELL validation
- Time validation
- BUY/SELL target and stop-loss transitions
- Expiry final state
- Final-state immutability
- BUY/SELL ROI calculations
