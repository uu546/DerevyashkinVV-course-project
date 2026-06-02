### Структура тестов

```java
src/test/java/project/warehouse/
├── entity/
│ ├── InventoryEntityTest.java
│ ├── ProductEntityTest.java
│ ├── MovementEntityTest.java
│ └── LocationEntityTest.java
├── foundation/
│ └── repositories/
│ ├── ProductRepositoryTest.java
│ ├── LocationRepositoryTest.java
│ ├── InventoryRepositoryTest.java
│ ├── MovementRepositoryTest.java
│ └── UserRepositoryTest.java
└── mediator/
└── services/
├── MovementServiceTest.java
└── AuthServiceTest.java
```

### Количество тестов

| Слой | Количество тестов | Пройдено | Успешность |
|------|-------------------|----------|------------|
| Entity тесты | 18 | 18 | 100% |
| Foundation тесты | 45 | 45 | 100% |
| Mediator тесты | 25 | 25 | 100% |
| **Итого** | **148** | **148** | **100%** |

## Покрытие кода (JaCoCo)
### О пороге покрытия
Целевой порог покрытия кода тестами установлен на уровне 40%. Исключены из анализа
control.dto пакет (DTO-объекты)
### Результаты покрытия
![alt text](/docs/06-testing/images/jacoco.png)

### Анализ результатов

| Показатель | Целевое значение | Достигнутое значение | Статус |
| :--- | :---: | :---: | :--- |
| **Общий Line Coverage** | ≥40% | 50% | ✅ Достигнуто |
| **Общий Branch Coverage** | ≥40% | 50% | ✅ Достигнуто |
| **Foundation слой** | — | 86% | ✅ Отлично |
| **Mediator слой** | — | 68% | ✅ Хорошо |
| **Entity слой** | — | 69% | ✅ Хорошо |
| **Security слой** | — | 43% | ✅ Хорошо |


### Вывод по покрытию

* **Целевой порог покрытия (40%) достигнут и превышен** — общий Line Coverage составляет 50%.
* **Наилучшее покрытие достигнуто в слое Foundation (86%)** — репозитории протестированы наиболее тщательно.
* **Хорошее покрытие в слоях Entity (69%) и Mediator (68%)** — бизнес-логика и сущности покрыты качественно.
* **Security слой имеет среднее покрытие (43%)** — основные механизмы аутентификации покрыты.