# Inventory Service

Product inventory service for OrderNest.

Local URL: `http://localhost:8081`  
Live URL: `https://ordernest-inventory-service.onrender.com`
API Gateway URL: `https://ordernest-api-gateway.onrender.com`

## What it does
- Returns product list and product details
- Supports paginated products
- Supports stock update endpoint
- Caches paginated responses in Redis (cache per page+size)

## Security model
- Inventory service does **not** validate JWT by itself.
- Authentication/authorization is handled at API Gateway.
- `PATCH /api/products/{id}/stock` is restricted to `ADMIN` in API Gateway.

## Run locally
```powershell
.\gradlew.bat bootRun
```

## Required config
- `DB_USERNAME`
- `DB_PASSWORD`

## Redis cache config
- `REDIS_HOST` (default: `localhost`)
- `REDIS_PORT` (default: `6379`)
- `REDIS_USERNAME` (optional)
- `REDIS_PASSWORD` (optional)

## API
Gateway base URL: `https://ordernest-api-gateway.onrender.com`
- `GET /api/products`
- `GET /api/products/paginated?page=0&size=10`
- `GET /api/products/{id}`
- `PATCH /api/products/{id}/stock`

Example stock update body:
```json
{
  "availableQuantity": 20
}
```

## Swagger
- Local Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- Local OpenAPI JSON: `http://localhost:8081/v3/api-docs`
- API calls should use gateway base URL: `https://ordernest-api-gateway.onrender.com`
- Live Swagger UI: `https://ordernest-inventory-service.onrender.com/swagger-ui/index.html`
- Live OpenAPI JSON: `https://ordernest-inventory-service.onrender.com/v3/api-docs`

## Health
- `http://localhost:8081/actuator/health`
- `https://ordernest-inventory-service.onrender.com/actuator/health`

## Postman
- `postman/ordernest-inventory-service.postman_collection.json`
