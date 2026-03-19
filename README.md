# Inventory Service

Spring Boot inventory service for OrderNest.  
Provides secured product APIs with PostgreSQL persistence and startup sample-data seeding.

## Tech Stack
- Java 17
- Spring Boot 3.3.5
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (`jjwt`)

## Features
- JWT-protected product APIs
- Product CRUD operations
- Stock-only update endpoint
- Validation + consistent error responses
- Auto-seed of 10 sample products on startup (idempotent by product name)

## Prerequisites
- JDK 17+
- PostgreSQL database
- Gradle wrapper (you can use wrapper from sibling service if this module has no wrapper)

## Configuration
File: `src/main/resources/application.yml`

Required env vars:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET` (must match auth service signing secret)

Optional:
- `app.seed.enabled` (default `true`)

Current JPA mode:
- `spring.jpa.hibernate.ddl-auto=update`

## Run Locally
From this directory:

```powershell
..\sso-service\gradlew.bat -p . bootRun
```

Default URL: `http://localhost:8080`

## Authentication
All product endpoints require:

```http
Authorization: Bearer <jwt-token>
```

Use token from `sso-service` login.

## API
Base path: `/api/products`

1. `GET /api/products`
- List all products

2. `GET /api/products/{id}`
- Fetch one product by UUID

3. `POST /api/products`
- Create product

Request body:
```json
{
  "name": "Laptop",
  "price": 75000,
  "currency": "INR",
  "availableQuantity": 10,
  "description": "High-performance laptop suitable for development and gaming."
}
```

4. `PUT /api/products/{id}`
- Full product update

5. `PATCH /api/products/{id}/stock`
- Update stock only

Request body:
```json
{
  "availableQuantity": 20
}
```

6. `DELETE /api/products/{id}`
- Delete product

## Error Responses
- `400` validation errors
- `401` unauthorized / missing token
- `404` product not found
- `409` duplicate product name
- `500` unexpected server error

## Seed Data Behavior
On startup, service checks the 10 default sample products by name:
- If name exists, it is skipped
- If missing, it is inserted

This makes seeding safe across restarts.
