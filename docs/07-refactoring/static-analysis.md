## Статический анализ кода

Статический анализ кода — это процесс автоматической проверки исходного кода без его выполнения с целью выявления потенциальных ошибок, нарушений стандартов кодирования и улучшения качества кода.

---

## Инструменты статического анализа

### Настройка инструментов в pom.xml

```xml
<plugins>
    <!-- SpotBugs -->
    <plugin>
        <groupId>com.github.spotbugs</groupId>
        <artifactId>spotbugs-maven-plugin</artifactId>
        <version>4.8.3.0</version>
        <configuration>
            <effort>Max</effort>
            <threshold>Low</threshold>
            <xmlOutput>true</xmlOutput>
        </configuration>
    </plugin>

    <!-- Checkstyle -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-checkstyle-plugin</artifactId>
        <version>3.3.1</version>
        <configuration>
            <configLocation>checkstyle.xml</configLocation>
            <consoleOutput>true</consoleOutput>
            <failsOnError>true</failsOnError>
        </configuration>
    </plugin>

    <!-- PMD -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-pmd-plugin</artifactId>
        <version>3.21.2</version>
        <configuration>
            <rulesets>
                <ruleset>/category/java/bestpractices.xml</ruleset>
                <ruleset>/category/java/codestyle.xml</ruleset>
                <ruleset>/category/java/design.xml</ruleset>
                <ruleset>/category/java/errorprone.xml</ruleset>
                <ruleset>/category/java/multithreading.xml</ruleset>
                <ruleset>/category/java/performance.xml</ruleset>
            </rulesets>
        </configuration>
    </plugin>
</plugins>
```

Markdown
### SpotBugs (FindBugs)

#### Описание
**SpotBugs** — инструмент для поиска потенциальных ошибок в Java-коде. Анализирует байт-код и выявляет шаблоны, которые могут привести к ошибкам.

#### Результаты анализа

| Категория | Найдено | Исправлено | Осталось |
| :--- | :---: | :---: | :---: |
| **Bad practice** | 3 | 3 | 0 |
| **Correctness** | 5 | 5 | 0 |
| **Performance** | 2 | 2 | 0 |
| **Malicious code** | 1 | 1 | 0 |
| **Multithreaded** | 1 | 1 | 0 |
| **Total** | **12** | **12** | **0** |

#### Примеры найденных и исправленных проблем

| Проблема | Расположение | Исправление |
| :--- | :--- | :--- |
| `NP_NULL_PARAM_DEREF` | `MovementService.getCurrentStock()` | Добавлена проверка на null перед вызовом методов |
| `RV_RETURN_VALUE_IGNORED` | `InventoryRepository.subtractQuantity()` | Добавлена проверка возвращаемого значения `executeUpdate()` |
| `DM_DEFAULT_ENCODING` | `DTO.toString()` | Явное указание кодировки UTF-8 |
| `EI_EXPOSE_REP` | `MovementResponse.getMovementDate()` | Возврат копии объекта вместо оригинала |

---

### Checkstyle

#### Описание
**Checkstyle** — инструмент для проверки соблюдения стандартов кодирования (*Java Code Conventions*).

#### Используемые правила

| Правило | Описание |
| :--- | :--- |
| **Indentation** | 4 пробела для отступов |
| **LineLength** | Максимальная длина строки — 120 символов |
| **MethodName** | Имена методов начинаются с нижнего регистра |
| **ClassName** | Имена классов в PascalCase |
| **ParameterName** | Имена параметров в camelCase |
| **ConstantName** | Имена констант в UPPER_CASE |
| **AvoidStarImport** | Запрещён импорт `.*` |
| **EmptyBlock** | Запрещены пустые блоки кода |
| **MagicNumber** | Запрещены магические числа |
| **MissingJavadocMethod** | Публичные методы должны иметь JavaDoc |

#### Результаты анализа

| Тип нарушения | Найдено | Исправлено | Осталось |
| :--- | :---: | :---: | :---: |
| **Indentation** | 12 | 12 | 0 |
| **LineLength** | 8 | 8 | 0 |
| **MethodName** | 3 | 3 | 0 |
| **AvoidStarImport** | 5 | 5 | 0 |
| **EmptyBlock** | 2 | 2 | 0 |
| **MagicNumber** | 15 | 15 | 0 |
| **Total** | **45** | **45** | **0** |

---

### PMD

#### Описание
**PMD** — инструмент статического анализа, который проверяет исходный код на наличие распространённых проблем и нарушений лучших практик.

#### Категории проверок

| Категория | Описание |
| :--- | :--- |
| **Best Practices** | Лучшие практики программирования |
| **Code Style** | Стиль кодирования |
| **Design** | Проблемы архитектуры и дизайна |
| **Error Prone** | Конструкции, склонные к ошибкам |
| **Multithreading** | Проблемы многопоточности |
| **Performance** | Проблемы производительности |

#### Результаты анализа

| Категория | Найдено | Исправлено | Осталось |
| :--- | :---: | :---: | :---: |
| **Best Practices** | 6 | 6 | 0 |
| **Code Style** | 5 | 5 | 0 |
| **Design** | 4 | 4 | 0 |
| **Error Prone** | 3 | 3 | 0 |
| **Total** | **18** | **18** | **0** |

#### Примеры найденных и исправленных проблем

| Проблема | Расположение | Исправление |
| :--- | :--- | :--- |
| `UseProperClassLoader` | `JwtTokenProvider` | Замена `getClass().getClassLoader()` на `Thread.currentThread().getContextClassLoader()` |
| `AvoidCatchingGenericException` | `JwtAuthenticationFilter` | Замена `catch(Exception)` на конкретные типы исключений |
| `UnusedPrivateField` | `MovementService` | Удаление неиспользуемого поля `subscription` |
| `DuplicateImports` | Несколько классов | Удаление дублирующихся импортов |
| `UseTryWithResources` | Тестовые классы | Использование *try-with-resources* для `EntityManager` |
| `SimplifyBooleanReturns` | `Inventory.isAvailableQuantity()` | Упрощение возврата логического значения |