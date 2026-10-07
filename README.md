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
cd duck-store
```

If you received the project as a ZIP, extract it and change into its folder instead:

```bash
cd "Duck Project"
```

2. Start the application. Maven downloads dependencies automatically on the first run:

```bash
mvn spring-boot:run
```

3. Open `http://localhost:8080` for the warehouse UI.

The H2 database is file-backed in `./data/duckstore`; it is created automatically. The H2 console is available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/duckstore`). Stop the application with `Ctrl+C` in the terminal.

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
- The database keeps one active row per color/size/price. The single-process POC additionally serializes add operations; the unique database constraint is a second guard.

## Design patterns

- **Strategy:** `WoodPackagingStrategy`, `CardboardPackagingStrategy`, and `PlasticPackagingStrategy` encapsulate package/protection behavior.
- **Factory:** `PackagingStrategyFactory` selects the strategy for the ordered duck size.
- **Chain of Responsibility:** `PricingCalculator` runs ordered `PricingRule` components: volume discount, package adjustment, destination adjustment, and shipping charge.
- **Repository:** `DuckRepository` isolates JPA queries and row locking.
- **Service layer:** `DuckService` and `OrderService` contain use-case logic; controllers only handle HTTP.
- **DTOs:** request/response records keep API contracts separate from the `Duck` entity.

## Project layout

- `duck`: entity, repository, CRUD controller/service, and request validation.
- `store`: pure packaging and pricing components plus the order endpoint.
- `static/index.html`: minimal warehouse UI.
- `src/test`: focused rule tests.

## Out of scope

Authentication, a store UI, inventory reservation, migrations, distributed locking, and deployment configuration are deliberately excluded because the assignment does not request them.
