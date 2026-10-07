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

1. Obtain the repository. Once this project is pushed, use its Git URL:

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
mvn test
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

## Deliberate decisions for ambiguities

- When multiple active rows share an order's color and size, the API uses the lowest unit price (then oldest ID) and does not decrement warehouse stock; inventory reservation was not requested.
- Percentage adjustments are each calculated from the merchandise subtotal (`unit price × quantity`); shipping is then added. This avoids hidden compounding and gives an auditable breakdown.
- Money uses `BigDecimal`, with line and final amounts rounded to two decimals using half-up rounding.
- Adding a duck identical to a logically deleted row restores that row and adds its quantity. This preserves history without duplicates.
- Price edits that collide with a different active identity are currently rejected by the database's identity constraint. This POC does not silently merge on edit because merging is only specified for add.
- The database unique constraint guarantees one row per color/size/price. A duplicate-key insert race retries once in a fresh transaction and merges quantities; this works across application instances sharing the same database.
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
