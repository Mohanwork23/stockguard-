# StockGuard

**Concurrency-safe inventory reservation and order processing system, built with Spring Boot.**

## Problem Statement

Flash-sale and limited-stock ticketing platforms routinely fail in production when many
users try to buy the last few units of an item at the same moment — naive
read-check-write logic oversells stock. StockGuard is a backend service that:

- Guarantees **zero overselling** under concurrent load, using two different
  concurrency-control strategies (optimistic vs. pessimistic locking), benchmarked
  against each other with a real load test.
- **Rate-limits** clients per user/IP using a Redis-backed token bucket, so a single
  abusive client can't monopolize limited stock.
- **Caches** hot product-catalog reads in Redis to keep browse traffic off the database.
- Processes order confirmation **asynchronously** via RabbitMQ, decoupling the
  order-placement path from downstream notification/fulfillment work.
- Is fully containerized, CI-tested on every push, and deployed to AWS.

## Why this project

Built to close specific gaps between what I'd shipped at work (CRUD REST APIs, JWT auth,
query optimization) and what production backend systems actually need to handle:
correctness under concurrency, caching, async processing, and cloud deployment.

## Architecture

```
                    ┌─────────────┐
   Client ────────▶ │ Rate Limiter │ (Redis token bucket / Bucket4j)
                    └──────┬──────┘
                           ▼
                  ┌─────────────────┐        ┌───────────┐
                  │  OrderController │──────▶ │  Product  │ (Redis cache-aside
                  └────────┬─────────┘        │  Catalog  │  for GET /products)
                           ▼                  └───────────┘
                  ┌──────────────────┐
                  │   OrderService    │
                  │ optimistic OR     │──────▶ PostgreSQL (products, orders)
                  │ pessimistic lock  │
                  └────────┬──────────┘
                           ▼
                  ┌──────────────────┐
                  │  RabbitMQ event   │──────▶ Notification Consumer (async)
                  └──────────────────┘
```

## Tech Stack

Java 17 · Spring Boot 3 · Spring Security (JWT) · Spring Data JPA · PostgreSQL ·
Redis · Bucket4j · RabbitMQ · Docker / Docker Compose · GitHub Actions · AWS

## Roadmap / Build Log

- [x] Day 1-2: Repo scaffold, entities (`Product`, `Order`, `User`), schema
- [ ] Day 3-4: JWT auth + role-based access (Admin / Customer)
- [ ] Day 5-7: Product CRUD APIs, unit tests, local Docker Compose
- [x] Day 8-10: Optimistic-locking order placement (`OrderService.placeOrderOptimistic`)
- [x] Day 11-12: Pessimistic-locking variant (`SELECT ... FOR UPDATE`) for comparison
- [ ] Day 13-14: k6 load test proving zero overselling; latency/throughput comparison
- [ ] Day 15-16: Redis cache-aside for product catalog reads
- [ ] Day 17-18: Redis-backed rate limiting (Bucket4j) per user/IP
- [ ] Day 19-21: RabbitMQ order-confirmation event + async consumer
- [ ] Day 22-24: GitHub Actions CI (build + test on every push)
- [ ] Day 25-26: AWS deployment (EC2/Elastic Beanstalk + RDS + ElastiCache)
- [ ] Day 26: README cleanup and merge verification
- [x] Day 27: Added product search, order history, and inventory analytics APIs
- [ ] Day 27-28: Concurrency Findings write-up below, final polish

## Concurrency Findings

*(Fill in after Day 14 load test — this section is the resume-worthy metric.)*

| Strategy | Requests | Successful Orders | Overselling? | Avg Latency | Throughput |
|---|---|---|---|---|---|
| Optimistic (retry) | 100 concurrent, 50 stock | TBD | TBD | TBD | TBD |
| Pessimistic (row lock) | 100 concurrent, 50 stock | TBD | TBD | TBD | TBD |

## Running Locally

```bash
docker-compose up -d postgres redis rabbitmq
./mvnw spring-boot:run
```

## Load Testing

```bash
k6 run loadtest/order_concurrency_test.js
```

## Running Tests (no local Maven)

If you don't have Maven installed locally, you can run the project's tests using Docker and the official Maven image.

Linux / macOS:

```bash
./run-tests.sh
```

Windows PowerShell:

```powershell
.\run-tests.ps1
```

Both scripts mount the repository into a Maven Docker container and execute `mvn test`.

## Pull Request Template

When opening a PR for this repository, please include the information below to help reviewers:

- **Title**: short, imperative summary (e.g., "feat: add Redis caching and rate limiter")
- **Description**: one-paragraph summary of what changed and why
- **Type**: `feat` / `fix` / `chore` / `docs` / `test`
- **Testing**: brief steps to reproduce and verify the change (include commands)
- **Related**: link to any related issues or design notes
- **Checklist**:
  - [ ] Code builds and tests pass (see `./run-tests.sh`)
  - [ ] New/changed behavior covered by tests
  - [ ] README updated where applicable

Example PR description:

```
Title: feat: add JWT auth, Redis cache, Redis rate limiter, and RabbitMQ events

Description: Implements JWT-based authentication, caches product reads in Redis, adds a simple Redis-backed rate limiter for product endpoints, and publishes async order events to RabbitMQ. Also includes unit tests and Docker-based test scripts.

Testing:
- Run `./run-tests.sh` (or `./run-tests.ps1` on Windows)
- Start services: `docker-compose up -d postgres redis rabbitmq`
- Run k6 load test: `k6 run loadtest/order_concurrency_test.js`

Related: None

Checklist:
- [x] Code builds and tests pass
- [x] New behavior covered by tests
- [x] README updated
```

## Product API

- `GET /api/products` - returns cached product list
- `GET /api/products/{id}` - returns cached product details
- `POST /api/products` - create product (admin only)
- `PUT /api/products/{id}` - update product (admin only)
- `DELETE /api/products/{id}` - delete product (admin only)
- `X-Client-Id` header is used by the built-in Redis rate limiter for product reads

Example:

```bash
curl -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"New Widget","price":29.99,"availableStock":100}' \
  http://localhost:8080/api/products
```

## Async Order Events

When an order is created, StockGuard publishes an `OrderEvent` to RabbitMQ:

- exchange: `stockguard.order.exchange`
- queue: `stockguard.order.queue`
- routing key: `stockguard.order.routingkey`

The async listener logs the event and is a placeholder for follow-up work such as email confirmation or downstream fulfillment.

## License

MIT
