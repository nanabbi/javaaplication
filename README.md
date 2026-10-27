# Full Stack Web Application with Spring Boot & Microservices

A complete, working end-to-end full stack microservices application built with Spring Boot 3, embedded in-memory H2 databases, Spring Data JPA, and a responsive Tailwind CSS web frontend.

---

## Architecture Overview

```
                                +---------------------------+
                                |  Web Frontend Dashboard   |
                                |   (http://localhost:3000) |
                                +-------------+-------------+
                                              |
                     +------------------------+------------------------+
                     |                                                 |
                     v                                                 v
         +-----------------------+                         +-----------------------+
         |  Product Microservice |                         |   Order Microservice  |
         | (Spring Boot :8081)   | <---- REST Client ----- | (Spring Boot :8082)   |
         +-----------+-----------+                         +-----------+-----------+
                     |                                                 |
                     v                                                 v
             [(Embedded H2 DB)]                                [(Embedded H2 DB)]
```

### Components:
1. **Product Catalog Microservice (`:8081`)**:
   - Manages products and stock inventory.
   - Built-in embedded **H2 relational database** with JPA/Hibernate auto-DDL.
   - H2 Web Console accessible at `http://localhost:8081/h2-console`.
   - Inventory reservation API: `POST /api/products/{id}/reduce-stock`.

2. **Order Processing Microservice (`:8082`)**:
   - Manages customer orders and transactions.
   - Dedicated embedded **H2 relational database**.
   - Inter-service communication via Spring `RestTemplate` (queries Product Service for live unit price and reduces stock during order placement).
   - H2 Web Console accessible at `http://localhost:8082/h2-console`.

3. **Full Stack Frontend Dashboard (`:3000`)**:
   - Modern, responsive Single Page Application (SPA).
   - Live service health ping indicators (Online / Offline status).
   - Product catalogue management with instant seed generator.
   - Order placement with real-time stock and price synchronization.

---

## Quick Access & Live URLs

- **Frontend Application**: [http://localhost:3000](http://localhost:3000)
- **Product Microservice API**: [http://localhost:8081/api/products](http://localhost:8081/api/products)
- **Order Microservice API**: [http://localhost:8082/api/orders](http://localhost:8082/api/orders)
- **Product H2 Console**: [http://localhost:8081/h2-console](http://localhost:8081/h2-console) (JDBC URL: `jdbc:h2:mem:productdb`, User: `sa`, Password: *(empty)*)
- **Order H2 Console**: [http://localhost:8082/h2-console](http://localhost:8082/h2-console) (JDBC URL: `jdbc:h2:mem:orderdb`, User: `sa`, Password: *(empty)*)

---

## One-Click Startup Script

To restart all services at any time, run:
```powershell
.\start-all.bat
```
