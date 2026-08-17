# 🚚 Ermiş Market Courier Tracking Service

A real-time courier location tracking backend built with **Java 17** and **Spring Boot 3**.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Design Patterns](#-design-patterns)
- [Quick Start](#-quick-start)
- [Running with Docker](#-running-with-docker)
- [Running Locally (Maven)](#-running-locally-maven)
- [API Reference](#-api-reference)
- [Testing](#-testing)
- [Project Structure](#-project-structure)
- [Business Logic](#-business-logic)
- [Configuration](#️-configuration)

---

## ✨ Features

| Feature | Description |
|---|---|
| 📍 **Real-time Location Ingestion** | REST endpoint to stream courier GPS coordinates |
| 🏪 **Store Proximity Detection** | Detects when a courier enters 100m radius of a Ermiş Market store |
| ⏱️ **Re-entry Cooldown** | Prevents duplicate store entry logs within 1 minute |
| 📏 **Total Distance Tracking** | Accumulates travel distance using the Haversine formula |
| 📚 **Swagger UI** | Interactive API documentation |
| 🗄️ **H2 Console** | In-browser database inspector |

---

## 🛠 Tech Stack

- **Java 17** + **Spring Boot 3.2.5**
- **Spring Data JPA** + **H2** (in-memory database)
- **SpringDoc OpenAPI** (Swagger UI)
- **Lombok** for boilerplate reduction
- **JUnit 5** + **Mockito** for testing
- **Docker** + **Docker Compose** for containerization

---

## 📐 Design Patterns

This project applies core GoF design patterns to ensure clean, maintainable, and loosely coupled code:

- **Observer Pattern:** Implemented via Spring's `ApplicationEventPublisher`. When a courier location is processed, a `CourierLocationUpdatedEvent` is fired. Independent observer classes (`DistanceAccumulationObserver` and `StoreProximityObserver`) listen for this event and perform their respective logic asynchronously and independently.
- **Strategy Pattern:** Distance calculation is decoupled behind a `DistanceStrategy` interface. The `HaversineDistanceStrategy` is injected as the concrete implementation, allowing future algorithms (like Google Maps API distance) to be swapped in easily without modifying the core service.

---

## ⚡ Quick Start

### Prerequisites

| Tool | Minimum Version | Download |
|---|---|---|
| Java JDK | 17 | https://adoptium.net/ |
| Maven | 3.8+ | https://maven.apache.org/ |
| Docker *(optional)* | 20+ | https://www.docker.com/ |

---

## 🐳 Running with Docker

> **Easiest method** — no Java or Maven installation required.

```bash
# 1. Clone / navigate to project
cd ermis_market_java_case

# 2. Build and start
docker compose up --build

# 3. Application is ready at http://localhost:8080
```

To stop:
```bash
docker compose down
```

---

## 💻 Running Locally (Maven)

### Windows

```bat
mvn clean package -DskipTests
java -jar target\courier-tracking-1.0.0.jar
```

### Linux / macOS

```bash
mvn clean package -DskipTests
java -jar target/courier-tracking-1.0.0.jar
```

### Application URLs

Once running, visit:

| URL | Description |
|---|---|
| `http://localhost:8080/swagger-ui.html` | 🔵 **Swagger UI** — Interactive API Docs |
| `http://localhost:8080/h2-console` | 🟠 **H2 Console** — Database Browser |
| `http://localhost:8080/api-docs` | 🟢 **OpenAPI JSON** — Raw API Spec |

> **H2 Console credentials:**
> - JDBC URL: `jdbc:h2:mem:courierdb`
> - Username: `sa`
> - Password: *(leave blank)*

---

## 📡 API Reference

### POST `/api/couriers/locations`
Submit a courier's real-time location.

**Request Body:**
```json
{
  "courierId": "courier-001",
  "latitude": 40.9923307,
  "longitude": 29.1244229,
  "timestamp": "2024-01-15T10:30:00"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Location recorded successfully"
}
```

---

### GET `/api/couriers/{courierId}/total-distance`
Get total travel distance for a courier.

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "courierId": "courier-001",
    "totalDistanceMeters": 1523.75,
    "totalDistanceKilometers": 1.52
  }
}
```

---

### GET `/api/couriers/{courierId}/store-entries`
Get all Ermiş Market store entry logs for a courier.

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "storeName": "Ataşehir MMM Ermiş Market",
      "storeLatitude": 40.9923307,
      "storeLongitude": 29.1244229,
      "entryTime": "2024-01-15T10:30:00"
    }
  ]
}
```

---

### GET `/api/stores`
List all loaded Ermiş Market stores.

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    { "id": 1, "name": "Ataşehir MMM Ermiş Market", "latitude": 40.9923307, "longitude": 29.1244229 },
    { "id": 2, "name": "Novada MMM Ermiş Market",   "latitude": 40.986106,  "longitude": 29.1161293 },
    { "id": 3, "name": "Beylikdüzü 5M Ermiş Market","latitude": 41.0066851, "longitude": 28.6552262 },
    { "id": 4, "name": "Ortaköy MMM Ermiş Market",  "latitude": 41.055783,  "longitude": 29.0210292 },
    { "id": 5, "name": "Caddebostan MMM Ermiş Market","latitude": 40.9632463,"longitude": 29.0630908 }
  ]
}
```

---

## 🧪 Testing

### Run all tests

```bash
mvn test
```

### Test suites included

| Test Class | Coverage |
|---|---|
| `HaversineDistanceStrategyTest` | Formula accuracy, edge cases, symmetry (Strategy Pattern) |
| `CourierServiceTest` | Location ingestion, invalid entity handling, event publishing |
| `CourierControllerIntegrationTest` | Full HTTP request/response cycle with real DB, event firing |

---

## 📁 Project Structure

```text
courier_tracking_ermis_market/
├── src/
│   ├── main/
│   │   ├── java/com/ermis_market/couriertracking/
│   │   │   ├── CourierTrackingApplication.java   ← Entry point
│   │   │   ├── config/
│   │   │   │   ├── DataLoader.java               ← Loads stores.json on startup
│   │   │   │   └── SwaggerConfig.java            ← OpenAPI config
│   │   │   ├── controller/                       ← REST API Endpoints
│   │   │   ├── dto/                              ← Request/Response DTOs
│   │   │   ├── entity/                           ← JPA Entities
│   │   │   ├── event/                            
│   │   │   │   └── CourierLocationUpdatedEvent.java ← Event class (Observer Subject)
│   │   │   ├── exception/                        ← Global error handling
│   │   │   ├── repository/                       ← Spring Data JPA repos
│   │   │   └── service/
│   │   │       ├── contract/                     ← Service interfaces
│   │   │       ├── impl/                         ← Service implementations
│   │   │       ├── observer/                     
│   │   │       │   ├── DistanceAccumulationObserver.java ← Accumulates distance (Observer)
│   │   │       │   └── StoreProximityObserver.java       ← Checks 100m radius (Observer)
│   │   │       └── strategy/                     
│   │   │           ├── DistanceStrategy.java             ← Strategy interface
│   │   │           └── HaversineDistanceStrategy.java    ← Haversine implementation
│   │   └── resources/
│   │       ├── application.yml                   ← App configuration
│   │       └── stores.json                       ← Ermiş Market store data
│   └── test/                                     ← Unit & integration tests
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

## 🧠 Business Logic

### Haversine Formula (Strategy Pattern)
The Earth's curved surface means straight-line distance formulas are inaccurate. The **Haversine formula** calculates the great-circle distance between two lat/lng points in meters.

```
a = sin²(Δlat/2) + cos(lat1) × cos(lat2) × sin²(Δlon/2)
c = 2 × atan2(√a, √(1−a))
d = R × c        (R = 6,371,000 meters)
```

### Event-Driven Proximity Flow (Observer Pattern)

```text
POST /api/couriers/locations
        │
        ├─► Save `CourierLocation` log
        │
        └─► Publish `CourierLocationUpdatedEvent`
                 │
                 ├─► [DistanceAccumulationObserver]
                 │        └─► Calculate distance using `DistanceStrategy`
                 │        └─► Accumulate into courier.totalDistance & update last pos
                 │
                 └─► [StoreProximityObserver]
                          └─► For each Ermiş Market store:
                                   ├─► distance ≤ 100m?
                                   │       ├─► YES → last entry > 1 min ago?
                                   │       │           ├─► YES → Save StoreEntry ✅
                                   │       │           └─► NO  → Skip (cooldown) ⏱️
                                   │       └─► NO  → Skip
```

---

## ⚙️ Configuration

Edit `src/main/resources/application.yml` to tune:

```yaml
courier:
  tracking:
    store-radius-meters: 100.0    # Proximity threshold (meters)
    reentry-cooldown-minutes: 1   # Cooldown before re-logging a store entry
```

---

## 🗺️ Preloaded Ermiş Market Stores

| Store | Latitude | Longitude |
|---|---|---|
| Ataşehir MMM Ermiş Market | 40.9923307 | 29.1244229 |
| Novada MMM Ermiş Market | 40.986106 | 29.1161293 |
| Beylikdüzü 5M Ermiş Market | 41.0066851 | 28.6552262 |
| Ortaköy MMM Ermiş Market | 41.055783 | 29.0210292 |
| Caddebostan MMM Ermiş Market | 40.9632463 | 29.0630908 |
