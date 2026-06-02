## Этап 6: Тестирование

### Цель этапа

Обеспечить качество и надёжность разработанного программного обеспечения путем написания модульных тестов для ключевых компонентов системы и анализа покрытия кода.

## Содержание

| №   | Раздел                   | Ссылка                               |
| --- | ------------------------ | ------------------------------------ |
| 1   | Модульное тестирование   | [Перейти](tests.md)   |
| 2   | Инструменты тестирования | [Перейти](#инструменты-тестирования) |
| 3   | Покрытие кода (JaCoCo)   | [Перейти](tests.md#покрытие-кода-jacoco)     |
| 4   | Результаты тестирования  | [Перейти](tests.md#результаты-покрытия)  |

**В рамках этапа решаются следующие задачи:**

1. **Написание модульных тестов для Entity слоя** — проверка бизнес-методов сущностей.

2. **Написание модульных тестов для Foundation слоя** — проверка репозиториев с использованием H2 in-memory database.

3. **Написание модульных тестов для Mediator слоя** — проверка сервисов с использованием Mockito для изоляции зависимостей.

4. **Анализ покрытия кода** — использование JaCoCo для оценки покрытия и достижения целевого порога (40%).

---

## Инструменты тестирования

| Инструмент | Версия | Назначение |
|------------|--------|------------|
| **JUnit 5** | 5.10 | Фреймворк для написания и запуска тестов |
| **Mockito** | 5.6 | Создание мок-объектов для изоляции зависимостей |
| **H2 Database** | 2.2 | In-memory БД для тестирования репозиториев |
| **JaCoCo** | 0.8.11 | Анализ покрытия кода тестами |
| **AssertJ** | 3.24 | Fluent assertions для читаемых проверок |

### Зависимости в pom.xml

```xml
<!-- Test dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>

<!-- JaCoCo Plugin -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.40</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
                <excludes>
                    <exclude>com.example.warehouse.entity.*</exclude>
                    <exclude>com.example.warehouse.control.dto.*</exclude>
                </excludes>
            </configuration>
        </execution>
    </executions>
</plugin>