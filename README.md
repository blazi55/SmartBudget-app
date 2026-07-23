# SmartBudget

Personal finance system with CQRS split:

- **backend-main** (port `8080`) – write model: users, categories, transactions, budgets
- **backend-report** (port `8081`) – read model: daily/monthly/category/trends reports via Kafka
- **frontend** (port `5173`) – dashboard + reports UI

## Quick start

### 1. Infrastructure (`budget-compose`)

From `backend-main`:

```bash
cd backend-main
podman compose -f podman/budget-compose.yaml up -d postgres-budget postgres-report kafka-budget
# or: docker compose -f podman/budget-compose.yaml up -d postgres-budget postgres-report kafka-budget
```

This starts:

| Service | Host port |
|---------|-----------|
| Postgres (budget) | `5432` |
| Postgres (report) | `5433` |
| Redpanda/Kafka | `9093` |

Credentials: `postgres` / `postgres`

### 2. Run apps from IDE (or Maven)

```bash
cd backend-main && mvn spring-boot:run
cd backend-report && mvn spring-boot:run
cd frontend && npm install && npm run dev
```

### 3. Optional: run apps via compose too

Build jars first, then start all services:

```bash
cd backend-main && mvn -DskipTests package
cd ../backend-report && mvn -DskipTests package
cd ../backend-main
podman compose -f podman/budget-compose.yaml up -d
```

### 4. Sync existing transactions into reports

```bash
curl -X POST http://localhost:8080/api/transactions/republish-events
```

Or use **Sync from Budget Service** on the Reports page.

## Report APIs

- `GET /api/reports/daily?userId=1`
- `GET /api/reports/monthly?userId=1`
- `GET /api/reports/categories?userId=1`
- `GET /api/reports/trends?userId=1`


<img width="1900" height="899" alt="image" src="https://github.com/user-attachments/assets/dbffd205-6faa-4535-b850-831c3eebb970" />
<img width="1843" height="889" alt="image" src="https://github.com/user-attachments/assets/f0502699-1ab2-4da6-9d27-1b300c3b82e8" />
<img width="1919" height="796" alt="image" src="https://github.com/user-attachments/assets/6ff98bc7-bbbb-436f-b598-4494b455619c" />
