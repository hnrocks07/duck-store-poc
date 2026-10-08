# Duck Store POC

A small Spring Boot warehouse application and REST order-pricing API for the Duck Store assignment. It intentionally prioritizes the required behavior, readability, and tests over production infrastructure.

## Requirements

- Java 21+
- Maven 3.9+

Check the installed tools:

```bash
java -version
mvn -version
```

## Fresh-machine setup and run

1. Clone the repository:

```bash
git clone https://github.com/hnrocks07/duck-store-poc.git
cd duck-store-poc
```

If you received the project as a ZIP, extract it and change into the extracted repository folder instead.

2. Start the application. Maven downloads dependencies automatically on the first run:

```bash
mvn spring-boot:run
```

3. Open `http://localhost:8080` for the warehouse UI.

The H2 database is file-backed in `./data/duckstore`; it is created automatically. The H2 console is disabled by default and can be enabled locally with `spring.h2.console.enabled=true`. Stop the application with `Ctrl+C` in the terminal.

## Test

```bash
mvn clean test
```

Tests cover merge behavior, concurrent same-duck additions, logical deletion/restoration, immutable identity on edit, package/protection rules, pricing calculations, volume and air-shipping thresholds.

## API

### Warehouse

`GET /api/ducks` lists non-deleted ducks, highest quantity first.

`POST /api/ducks`

```json
{"color":"Red","size":"Large","price":12.50,"quantity":5}
```

`PUT /api/ducks/{id}` only accepts editable fields:

```json
{"price":15.00,"quantity":8}
```

`DELETE /api/ducks/{id}` logically deletes it.

### Price an order

`POST /api/orders/price`

```json
{"color":"Red","size":"Large","quantity":101,"destinationCountry":"India","shippingMode":"Air"}
```

The response includes the package, protections, total, and each pricing line.

### Curl examples

```bash
curl http://localhost:8080/api/ducks
curl -X POST http://localhost:8080/api/ducks -H 'Content-Type: application/json' -d '{"color":"Red","size":"Large","price":12.50,"quantity":5}'
curl -X PUT http://localhost:8080/api/ducks/1 -H 'Content-Type: application/json' -d '{"price":15.00,"quantity":0}'
curl -X DELETE http://localhost:8080/api/ducks/1
curl -X POST http://localhost:8080/api/orders/price -H 'Content-Type: application/json' -d '{"color":"Red","size":"Large","quantity":1001,"destinationCountry":"India","shippingMode":"Air"}'
```

Use the ID returned by Add in the edit/delete commands. Price the order before deleting its warehouse row (or add it again). For a Red/Large duck priced at 10.00 USD, the 1001-unit India/Air response is:

```json
{
  "packageType": "Wood",
  "protections": ["Polystyrene balls"],
  "totalToPay": 35935.90,
  "breakdown": [
    {"label": "Merchandise", "amount": 10010.00},
    {"label": "Volume discount (20%)", "amount": -2002.00},
    {"label": "Wood package surcharge (5%)", "amount": 500.50},
    {"label": "Destination India adjustment (19%)", "amount": 1901.90},
    {"label": "Air shipping", "amount": 30030.00},
    {"label": "Air bulk discount (15%)", "amount": -4504.50}
  ]
}
```

There is no separate frontend build, database install, or required environment variable. The backend serves the page: open localhost, not the HTML file directly. The first Maven run needs internet access. For the optional database console, run `mvn spring-boot:run -Dspring-boot.run.arguments=--spring.h2.console.enabled=true`; use JDBC URL `jdbc:h2:file:./data/duckstore`, username `sa`, empty password.

Validation failures return 400 with `error` and field messages. Invalid enum responses name the field and allowed values. Missing ducks return 404, collisions 409, and unexpected failures 500 with details logged only on the server.

## Checklist verification and interview notes

| Task | Implementation and evidence | Interview explanation |
|---|---|---|
| 1. Concurrent add | DuckWriter transaction, one retry; ten-thread race repeated ten times | The unique key resolves first-insert races and a fresh transaction merges the losing request. |
| 2. Money | Rounded line sums and separate air discount; 540 combinations checked | The customer can add the displayed amounts and get the exact total. |
| 3. Patterns | Shipping strategies, package strategies, rate maps, ordered rules | Shipping owns cost and protections; pricing rules append independent adjustments. |
| 4. UI | One add/edit form, locked identity, Cancel, safe DOM rendering | One form prevents add and edit behavior from drifting apart. |
| 5. Edit | Active/deleted collisions rejected, zero quantity permitted; API tests | An edit cannot create an identity that later breaks logical deletion. |
| 6. Errors | Structured validation, enum details, neutral DB conflicts, logged 500 | Clients receive actionable messages without database internals. |
| 7. Orders | Cheapest active price, complete response integration test | Pricing is a quote and does not reserve or consume inventory. |
| 8. Verification | All 15 packaging combinations, isolated pricing rules, clean Maven test run | Tests emphasize boundaries and observable business outcomes. |

Adding a shipping mode requires its enum value and strategy implementation; Spring collects strategies into the map. Adding a pricing rule requires one ordered component. Package strategies are retained to keep the existing code structure, and only select package type.

## Deliberate decisions for ambiguities

- When multiple active rows share an order's color and size, the API uses the lowest unit price. Equal active prices cannot coexist for the same identity.
- Percentage adjustments are each calculated from the merchandise subtotal (`unit price × quantity`); shipping is then added. This avoids hidden compounding and gives an auditable breakdown.
- Money uses `BigDecimal`. Each line is rounded to two decimals using HALF_UP first; the total is the sum of those displayed amounts.
- Adding a duck identical to a logically deleted row restores that row and adds its quantity. This preserves history without duplicates.
- Price edits colliding with any other active or deleted row return 409; merge applies only to add.
- The database unique constraint includes color, size, price and deleted status, guaranteeing one active row per identity. Existing rows are locked while quantities change. A losing concurrent insert retries once after rollback in a fresh transaction and merges into the committed row. This uses database coordination and also applies to instances sharing that database; the automated race test exercises ten connections in one application, repeated ten times.
- Country matching is trimmed and case-insensitive. Only USA, Bolivia, and India have special rates; `US` and `United States` use the 15% default.
- The pricing endpoint does not check available stock or decrement stock because reservation is outside the assignment scope.
- IDs are `Long`, money is `BigDecimal`, and color/size are enums for validation.
- Editing permits quantity zero for out-of-stock items; adding requires at least one unit. An edit colliding with any active or deleted identity is rejected.

## Design patterns

- **Shipping Strategy:** each shipping mode owns its cost and protection behavior.
- **Package lookup:** `PackagingStrategyFactory` selects the package type for a size.
- **Ordered pricing pipeline:** small pricing rules apply independently and append their own breakdown line. Adding a shipping mode or pricing rule means adding one class.

## Project layout

- `duck`: entity, repository, CRUD controller/service, and request validation.
- `store`: pure packaging and pricing components plus the order endpoint.
- `static/index.html`: minimal warehouse UI.
- `src/test`: focused rule tests.

## Out of scope

Authentication, a store UI, inventory reservation, migrations, distributed locking, and deployment configuration are deliberately excluded because the assignment does not request them.
