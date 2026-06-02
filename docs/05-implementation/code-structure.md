## Структура проекта (соответствие PCMEF)

### Общая структура проекта

```
warehouse-management/
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── project/warehouse/
│ │ │ ├── WarehouseApplication.java # Точка входа Spring Boot
│ │ │ │
│ │ │ ├── control/ # Слой Control (REST Controllers)
│ │ │ │ ├── controllers/
│ │ │ │ │ ├── MovementController.java
│ │ │ │ │ ├── InventoryQueryController.java
│ │ │ │ │ └── AuthController.java
│ │ │ │ └── dto/
│ │ │ │ ├── ReceiptBatchRequest.java
│ │ │ │ ├── ReceiptItem.java
│ │ │ │ ├── ShipmentBatchRequest.java
│ │ │ │ ├── ShipmentItem.java
│ │ │ │ ├── MoveRequest.java
│ │ │ │ ├── MovementResponse.java
│ │ │ │ ├── InventorySummaryResponse.java
│ │ │ │ ├── WarehouseInventoryDto.java
│ │ │ │ ├── LocationInventoryDto.java
│ │ │ │ ├── ProductInventoryDto.java
│ │ │ │ ├── TotalSummaryDto.java
│ │ │ │ ├── ProductLocationStockDto.java
│ │ │ │ ├── AuthRequest.java
│ │ │ │ ├── AuthResponse.java
│ │ │ │ └── RegisterRequest.java
│ │ │ │
│ │ │ ├── mediator/ # Слой Mediator (Services)
│ │ │ │ ├── interfaces/
│ │ │ │ │ ├── IMovementService.java
│ │ │ │ │ ├── IInventoryQueryService.java
│ │ │ │ │ └── IAuthService.java
│ │ │ │ └── impl/
│ │ │ │ ├── MovementServiceImpl.java
│ │ │ │ ├── InventoryQueryServiceImpl.java
│ │ │ │ └── AuthServiceImpl.java
│ │ │ │
│ │ │ ├── entity/ # Слой Entity (JPA Entities)
│ │ │ │ ├── Product.java
│ │ │ │ ├── Category.java
│ │ │ │ ├── Unit.java
│ │ │ │ ├── Warehouse.java
│ │ │ │ ├── Location.java
│ │ │ │ ├── Type.java
│ │ │ │ ├── Temperature.java
│ │ │ │ ├── Inventory.java
│ │ │ │ ├── Movement.java
│ │ │ │ └── User.java
│ │ │ │
│ │ │ ├── foundation/ # Слой Foundation (Repositories)
│ │ │ │ ├── interfaces/
│ │ │ │ │ ├── IProductRepository.java
│ │ │ │ │ ├── ILocationRepository.java
│ │ │ │ │ ├── IInventoryRepository.java
│ │ │ │ │ ├── IMovementRepository.java
│ │ │ │ │ └── IUserRepository.java
│ │ │ │ └── impl/
│ │ │ │ ├── ProductRepositoryImpl.java
│ │ │ │ ├── LocationRepositoryImpl.java
│ │ │ │ ├── InventoryRepositoryImpl.java
│ │ │ │ ├── MovementRepositoryImpl.java
│ │ │ │ └── UserRepositoryImpl.java
│ │ │ │
│ │ │ └── security/ # Безопасность (JWT)
│ │ │ ├── SecurityConfig.java
│ │ │ ├── JwtTokenProvider.java
│ │ │ ├── JwtAuthenticationFilter.java
│ │ │ ├── UserDetailsImpl.java
│ │ │ └── UserDetailsServiceImpl.java
│ │ │
│ │ └── resources/
│ │ ├── application.properties
│ │ └── application-test.properties
│ │
│ └── test/
│ └── java/
│ └── project/warehouse/
│ ├── entity/
│ ├── foundation/
│ └── mediator/
│
└── pom.xml
```



### Соответствие слоям PCMEF

| Слой | Пакет | Классы | Назначение |
|------|-------|--------|------------|
| **Presentation (P)** | `presentation` (Angular) | Компоненты, страницы, сервисы | UI, взаимодействие с пользователем |
| **Control (C)** | `control.controllers` | `MovementController`, `InventoryQueryController`, `AuthController` | REST API, валидация DTO |
| **Control (C)** | `control.dto` | `ReceiptBatchRequest`, `MovementResponse`, `AuthResponse` и др. | Объекты передачи данных |
| **Mediator (M)** | `mediator.interfaces` | `IMovementService`, `IInventoryQueryService`, `IAuthService` | Интерфейсы бизнес-логики |
| **Mediator (M)** | `mediator.impl` | `MovementServiceImpl`, `InventoryQueryServiceImpl`, `AuthServiceImpl` | Реализация бизнес-логики |
| **Entity (E)** | `entity` | `Product`, `Inventory`, `Movement`, `Location`, `User` и др. | JPA-сущности, бизнес-методы |
| **Foundation (F)** | `foundation.interfaces` | `IProductRepository`, `IInventoryRepository`, `IMovementRepository` и др. | Интерфейсы доступа к данным |
| **Foundation (F)** | `foundation.impl` | `ProductRepositoryImpl`, `InventoryRepositoryImpl`, `MovementRepositoryImpl` и др. | Реализация доступа к данным (JPA) |

---

### Направление зависимостей

![alt text](/docs/05-implementation/images/i1.png)

## Правила зависимостей

Архитектура системы построена на принципе однонаправленных зависимостей. Каждый слой может обращаться только к нижележащему слою через определённые интерфейсы.

| Правило | Описание |
|----------|----------|
| **P → C** | Слой Presentation (Angular) взаимодействует со слоем Control посредством HTTP-запросов к REST API |
| **C → M** | Слой Control вызывает бизнес-логику через интерфейсы сервисов слоя Mediator |
| **M → F** | Слой Mediator обращается к слою Foundation через интерфейсы репозиториев |
| **F → E** | Слой Foundation использует сущности слоя Entity для работы с данными |
| **Отсутствие обратных зависимостей** | Entity не зависит от Foundation, Foundation не зависит от Mediator, Mediator не зависит от Control |

