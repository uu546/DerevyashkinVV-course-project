## Применённые паттерны проектирования

В проекте «Система управления складом» реализованы следующие паттерны проектирования, способствующие улучшению архитектуры, поддерживаемости и производительности.

---

## 1. Data Mapper (Data Mapper Pattern)

### Описание

**Data Mapper** — это архитектурный паттерн, который перемещает данные между объектами и базой данных, сохраняя их независимость друг от друга. Маппер не имеет ничего общего с логикой домена и просто переносит данные.

### Применение в проекте

В проекте используется **JPA (Hibernate)** как реализация паттерна Data Mapper. Сущности (Entity) полностью изолированы от логики доступа к данным, а маппинг осуществляется через аннотации.

### Пример реализации

```java
@Entity
@Table(name = "inventories")
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
    
    // Бизнес-методы и геттеры/сеттеры...
}
```

### Преимущества использования

| Преимущество | Описание |
| :--- | :--- |
| **Независимость** | Сущности не знают о способе их хранения |
| **Тестируемость** | Легко тестировать бизнес-логику без БД |
| **Гибкость** | Можно сменить СУБД, не меняя код сущностей |

---

### 2. Identity Map (Identity Map Pattern)

#### Описание
**Identity Map** — это паттерн, который обеспечивает, что каждый объект базы данных загружается только один раз и хранится в кэше. При повторном запросе того же объекта возвращается ссылка на уже загруженный объект.

#### Применение в проекте
Hibernate использует кэш первого уровня (*First Level Cache*), который является реализацией паттерна Identity Map. В рамках одной сессии (`EntityManager`) Hibernate гарантирует, что для одного и того же идентификатора будет возвращён один и тот же экземпляр объекта.

#### Пример работы
```java
// В рамках одной транзакции
Product product1 = entityManager.find(Product.class, 1L);
Product product2 = entityManager.find(Product.class, 1L);

// product1 и product2 — один и тот же объект в памяти
assert product1 == product2;
```

### 3. Lazy Load (Lazy Loading Pattern)

#### Описание
**Lazy Load (Отложенная загрузка)** — это паттерн, при котором связанные данные загружаются не в момент загрузки основного объекта, а только в момент первого обращения к ним.

#### Применение в проекте
В проекте используется `fetch = FetchType.LAZY` для всех связей `@OneToMany` и `@ManyToOne`. Это позволяет:

- Загружать только необходимые данные при первичном запросе
- Избегать проблем с производительностью (проблема N+1 запросов)
- Контролировать загрузку вручную с помощью `JOIN FETCH` при необходимости

#### Пример реализации

```java
@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)  // ← Отложенная загрузка
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)  // ← Отложенная загрузка
    private List<Inventory> inventories = new ArrayList<>();
    
    // ...
}
```

#### Пример использования с JOIN FETCH
```java
// Без JOIN FETCH — будет дополнительный запрос при обращении к category
List<Product> products = entityManager.createQuery("SELECT p FROM Product p", Product.class)
    .getResultList();
products.get(0).getCategory().getName(); // Дополнительный SELECT

// С JOIN FETCH — все данные загружаются одним запросом
List<Product> products = entityManager.createQuery(
    "SELECT p FROM Product p JOIN FETCH p.category", Product.class)
    .getResultList();
products.get(0).getCategory().getName(); // Уже загружено, дополнительных запросов нет
```
#### Преимущества использования

| Преимущество      | Описание |
|-------------------|----------|
| Производительность | Загрузка только необходимых данных |
| Гибкость          | Возможность явно указать, что нужно загрузить с помощью JOIN FETCH |
| Избежание N+1     | Правильное использование Lazy Load + JOIN FETCH решает проблему N+1 запросов |