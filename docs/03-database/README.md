## Этап 3: Проектирование базы данных

### Цель этапа

Спроектировать и реализовать реляционную базу данных для «Системы управления складом», обеспечивающую:

- целостность и непротиворечивость данных;
- оптимальную производительность запросов;
- поддержку требований предметной области;
- возможность объектно-реляционного отображения (ORM) через JPA.

## Содержание

| № | Раздел | Ссылка |
|---|--------|--------|
| 1 | Логическая модель данных (ER-диаграмма) | [Перейти](#логическая-модель-данных-er-диаграмма) |
| 3 | Описание таблиц | [Перейти](#описание-таблиц) |
| 4 | Индексы и стратегии индексирования | [Перейти](#индексы-и-стратегия-индексирования) |
| 5 | Маппинг JPA-сущностей | [Перейти](#маппинг-jpa-сущностей) |
| 6 | DDL скрипты | [ddl.sql](ddl.sql) |

**В рамках этапа решаются следующие задачи:**

1. **Построение логической модели данных** — определение сущностей, атрибутов, связей и первичных/внешних ключей.

2. **Нормализация схемы** — обеспечение соответствия 3 нормальной форме (3НФ).

3. **Создание DDL-скриптов** — реализация физической модели в PostgreSQL.

4. **Оптимизация запросов через индексы** — выбор стратегий индексирования для часто используемых запросов.

5. **Реализация JPA-маппинга** — отображение таблиц на Java-классы (Entity) с аннотациями.

**Результат этапа:** Рабочая база данных, DDL-скрипты и JPA-сущности, готовые к использованию в слое Foundation архитектуры PCMEF.

---

## Логическая модель данных (ER-диаграмма)

![alt text](/docs/03-database/images/logic-er.png)

## Описание таблиц

### Справочные таблицы

Справочные таблицы содержат неизменяемые данные, используемые для классификации.

| Таблица | Описание | Ключевые поля |
|----------|----------|---------------|
| **categories** | Категории товаров для группировки товаров | id (PK), title |
| **units** | Единицы измерения товаров (шт., кг, л, м, пачка и др.) | id (PK), title (UNIQUE) |
| **types** | Типы ячеек хранения (стеллаж, паллета, холодильник, морозильная камера.) | id (PK), title |
| **temperatures** | Температурные режимы хранения товаров | id (PK), title |
| **warehouses** | Склады — физические места хранения товаров | id (PK), name, address |

### Основные таблицы

Основные таблицы содержат данные системы складского учёта.

| Таблица | Описание | Ключевые поля |
|----------|----------|---------------|
| **products** |  товар | id (PK), name, description, is_perishable, expiry_days, category_id (FK), unit_id (FK) |
| **locations** | Ячейки хранения товаров на складах | id (PK), name, warehouse_id (FK), type_id (FK), temperature_id (FK) |
| **inventories** | Текущие остатки товаров в ячейках хранения | id (PK), product_id (FK), location_id (FK), quantity, UNIQUE(product_id, location_id) |
| **movements** | История движения товаров (приёмка, отгрузка, перемещение) | id (PK), product_id (FK), from_location_id (FK), to_location_id (FK), quantity, movement_date |


## Связи между таблицами

Связи определяют структуру данных и обеспечивают целостность предметной области.

| Связь | Тип | Пояснение |
|--------|------|-----------|
| **categories → products** | 1 : N | Одна категория может содержать множество товаров |
| **units → products** | 1 : N | Одна единица измерения может использоваться у множества товаров |
| **warehouses → locations** | 1 : N | Один склад может содержать множество ячеек хранения |
| **types → locations** | 1 : N | Один тип может использоваться у множества ячеек хранения |
| **temperatures → locations** | 1..N | Температурный режим ячейки |
| **products → inventories** | 1 : N | Один товар может храниться в нескольких ячейках |
| **locations → inventories** | 1 : N | В одной ячейке может храниться несколько товаров |
| **products → movements** | 1 : N | Один товар может участвовать во множестве операций движения |
| **locations → movements (from)** | 0..1 | Исходная ячейка отсутствует при операции приёмки |
| **locations → movements (to)** | 0..1 | Целевая ячейка отсутствует при операции отгрузки |

## Индексы и стратегия индексирования

### Цели индексирования

- Ускорение поиска по внешним ключам (FOREIGN KEY)
- Ускорение фильтрации и сортировки данных
- Поддержка ограничений уникальности
- Повышение производительности JOIN-операций
- Оптимизация формирования отчётов

### Перечень индексов

| Таблица | Индекс | Колонки | Тип | Назначение |
|----------|---------|----------|------|------------|
| inventories | idx_inventories_product_id | product_id | B-tree | Поиск остатков конкретного товара |
| inventories | idx_inventories_location_id | location_id | B-tree | Поиск остатков в конкретной ячейке |
| locations | idx_locations_warehouse_id | warehouse_id | B-tree | Поиск ячеек определённого склада |
| locations | idx_locations_type_id | type_id | B-tree | Фильтрация по типу ячейки |
| locations | idx_locations_temperature_id | temperature_id | B-tree | Фильтрация по температурному режиму |
| locations | idx_locations_name | name | B-tree | Поиск ячейки по имени |
| movements | idx_movements_product_id | product_id | B-tree | Получение истории движения товара |
| movements | idx_movements_from_location_id | from_location_id | B-tree | Поиск перемещений из конкретной ячейки |
| movements | idx_movements_to_location_id | to_location_id | B-tree | Поиск перемещений в конкретную ячейку |
| movements | idx_movements_movement_date | movement_date | B-tree | Формирование отчётов за период |
| products | idx_products_category_id | category_id | B-tree | Фильтрация товаров по категории |
| products | idx_products_name | name | B-tree | Поиск товаров по наименованию |
| products | idx_products_is_perishable | is_perishable | B-tree | Фильтрация скоропортящихся товаров |


### Стратегия индексирования

| Стратегия | Описание |
|------------|----------|
| **B-tree (по умолчанию)** | Используется для всех индексов, поскольку большинство запросов используют операции сравнения (=, <, >, BETWEEN) |
| **Составные индексы** | В текущей версии не требуются, так как фильтрация выполняется преимущественно по одному полю. Для пары (product_id, location_id) используется уникальное ограничение |
| **Индексы на внешние ключи** | Обязательны для повышения производительности JOIN-запросов и предотвращения полного сканирования таблиц |
| **Индексы по датам** | Используются для ускорения формирования отчётов и выборок за заданный период времени |

## Маппинг JPA-сущностей

### Category.java
```java
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();
}
```

### Unit.java
```java
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "units")
public class Unit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String title;
    
    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();
}
```

### Product.java
```java
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 255)
    private String description;
    
    @Column(name = "is_perishable")
    private Boolean isPerishable = false;
    
    @Column(name = "expiry_days")
    private Integer expiryDays;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Inventory> inventories = new ArrayList<>();
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Movement> movements = new ArrayList<>();
}
```

### Warehouse.java
```java
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouses")
public class Warehouse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 255)
    private String address;
    
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<Location> locations = new ArrayList<>();
}
```

### Type.java
```java
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "types")
public class Type {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 50)
    private String title;
    
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private List<Location> locations = new ArrayList<>();
}
```

### Temperature.java
```java
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "temperatures")
public class Temperature {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 50)
    private String title;
    
    @OneToMany(mappedBy = "temperature", cascade = CascadeType.ALL)
    private List<Location> locations = new ArrayList<>();
}
```

### Location.java
```java
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations")
public class Location {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 50)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private Type type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "temperature_id")
    private Temperature temperature;
    
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<Inventory> inventories = new ArrayList<>();
    
    @OneToMany(mappedBy = "fromLocation", cascade = CascadeType.ALL)
    private List<Movement> movementsFrom = new ArrayList<>();
    
    @OneToMany(mappedBy = "toLocation", cascade = CascadeType.ALL)
    private List<Movement> movementsTo = new ArrayList<>();
}
```

### Inventory.java
```java
import jakarta.persistence.*;

@Entity
@Table(name = "inventories", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"product_id", "location_id"})
})
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    
    @Column(nullable = false)
    private Integer quantity = 0;
    
    // Бизнес-методы
    public void addQuantity(int amount) {
        if (amount > 0) this.quantity += amount;
    }
    
    public void subtractQuantity(int amount) {
        if (amount > 0 && this.quantity >= amount) {
            this.quantity -= amount;
        } else {
            throw new IllegalArgumentException("Недостаточно товара на складе");
        }
    }
    
    public boolean isAvailableQuantity(int requested) {
        return this.quantity >= requested;
    }
}
```

### Movement.java
```java
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "movements")
public class Movement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_location_id")
    private Location fromLocation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_location_id")
    private Location toLocation;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "movement_date", nullable = false)
    private LocalDate movementDate = LocalDate.now();
    
    // Бизнес-методы
    public boolean isReceipt() {
        return fromLocation == null;
    }
    
    public boolean isShipment() {
        return toLocation == null;
    }
    
    public boolean isTransfer() {
        return fromLocation != null && toLocation != null;
    }
}
```

## Стратегия маппинга

Стратегия ORM-маппинга определяет правила преобразования объектов предметной области в структуры реляционной базы данных и обеспечивает эффективную работу приложения с данными.

| Стратегия | Применение | Обоснование |
|-----------|------------|-------------|
| **LAZY-загрузка** | Все связи `@OneToMany` и `@ManyToOne` | Позволяет загружать связанные данные только при необходимости, снижает объём передаваемых данных и помогает избежать проблемы N+1 запросов |
| **IDENTITY генерация идентификаторов** | `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Использует встроенный механизм автоинкремента PostgreSQL, обеспечивая простую и надёжную генерацию первичных ключей |
| **Уникальные ограничения** | `@UniqueConstraint` для таблицы `inventories` | Гарантирует уникальность пары `(product_id, location_id)` и предотвращает создание дублирующихся записей об остатках |
| **Каскадирование операций** | `CascadeType.ALL` для родительских сущностей | Автоматизирует сохранение, обновление и удаление связанных сущностей, уменьшая количество дополнительного кода |