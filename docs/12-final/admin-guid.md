## Руководство администратора
### 1. Системные требования
#### Аппаратные требования

| Компонент | Минимальные требования | Рекомендуемые требования |
|-----------|------------------------|--------------------------|
| **Процессор** | 2 ядра, 2.0 GHz | 4 ядра, 2.5 GHz |
| **Оперативная память** | 4 GB | 8 GB |
| **Дисковое пространство** | 10 GB (SSD) | 20 GB (SSD) |
| **Сеть** | 100 Mbps | 1 Gbps |

#### Программные требования

| Компонент | Версия | Примечание |
|-----------|--------|------------|
| **Java** | 17 или 21 | OpenJDK / Oracle JDK |
| **PostgreSQL** | 15+ | Система управления базами данных |
| **Maven** | 3.8+ | Сборка бэкенд-приложения |
| **Node.js** | 18+ | Для сборки фронтенда (опционально) |
| **Angular CLI** | 17+ | Для сборки фронтенда (опционально) |
| **Браузер** | Современный | Chrome, Firefox, Edge, Safari |

---

### 2. Развёртывание серверной части
#### 2.1. Установка Java
**Windows:**
1. Скачайте JDK 17 с официального сайта Oracle или OpenJDK
2. Установите, следуя инструкциям установщика
3. Добавьте `JAVA_HOME` в переменные среды

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
java -version
```

## Установка PostgreSQL

### Windows

- Скачайте установщик PostgreSQL с официального сайта
- Установите, запомнив пароль суперпользователя postgres

### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

## Настройка базы данных

### Шаг 1: Создание базы данных

Подключитесь к PostgreSQL и выполните команды:

```bash
sudo -u postgres psql
```

```sql
-- Создание базы данных
CREATE DATABASE warehouse_db;

-- Создание пользователя (опционально)
CREATE USER warehouse_user WITH PASSWORD 'strong_password';

-- Назначение прав
GRANT ALL PRIVILEGES ON DATABASE warehouse_db TO warehouse_user;

-- Выход
\q
```

### Шаг 2: Выполнение DDL-скрипта

```bash
# Подключение к базе данных
psql -U postgres -d warehouse_db -f ddl.sql
```

Или через pgAdmin:

- Откройте pgAdmin
- Подключитесь к серверу
- Откройте Query Tool
- Выполните содержимое файла ddl.sql

## Настройка бэкенд-приложения

### Шаг 1: Конфигурация application.properties

Файл: src/main/resources/application.properties

```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/warehouse_db
spring.datasource.username=postgres
spring.datasource.password=your_password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
app.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
app.jwt.expiration=86400000

# CORS Configuration
spring.web.cors.allowed-origins=http://localhost:4200
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true

# Logging
logging.level.project.warehouse=DEBUG
logging.level.org.hibernate.SQL=INFO
```

### Шаг 2: Сборка приложения

```bash
cd warehouse-management-api
mvn clean package
```

### Шаг 3: Запуск приложения

```bash
# Через Maven
mvn spring-boot:run

# Или через JAR-файл
java -jar target/warehouse-management-api-1.0.0.jar
```

Приложение будет доступно по адресу: http://localhost:8080

## Настройка фронтенд-приложения

### Шаг 1: Установка зависимостей

```bash
cd warehouse-frontend
npm install
```

### Шаг 2: Настройка API endpoint

Файл: src/environments/environment.prod.ts

```typescript
export const environment = {
  production: true,
  apiUrl: 'http://your-server:8080/api'
};
```

### Шаг 3: Сборка приложения

```bash
ng build --prod
```

Собранное приложение будет в директории dist/warehouse-frontend/

### Шаг 4: Развёртывание на веб-сервере (Nginx)

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /var/www/warehouse-frontend;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## Конфигурация приложения

### Настройка CORS

Для обеспечения доступа из браузера необходимо настроить CORS. В SecurityConfig.java:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://your-domain.com"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### Настройка JWT

| Параметр | Значение | Описание |
|----------|----------|----------|
| app.jwt.secret | 512-битный ключ | Секретный ключ для подписи токенов |
| app.jwt.expiration | 86400000 (24 часа) | Время жизни токена в миллисекундах |

Генерация секретного ключа:

```bash
# Генерация 512-битного ключа
openssl rand -base64 64
```

### Настройка логирования

Файл: src/main/resources/logback-spring.xml

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/warehouse.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/warehouse.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

## Создание пользователя с ролью MANAGER

По умолчанию при регистрации создаются пользователи с ролью USER. Для создания пользователя с ролью MANAGER выполните SQL-запрос:

```sql
INSERT INTO users (email, password, full_name, role, is_active)
VALUES ('admin@example.com', '$2a$10$...', 'Администратор', 'MANAGER', true);
```

Либо измените роль существующего пользователя:

```sql
UPDATE users SET role = 'MANAGER' WHERE email = 'user@example.com';
```

**Важно:** Пароль должен быть захеширован с помощью BCrypt. Для генерации хеша можно использовать онлайн-сервисы или утилиты Spring Security.

## Резервное копирование и восстановление

### Резервное копирование базы данных

```bash
# Создание дампа базы данных
pg_dump -U postgres -d warehouse_db > backup_$(date +%Y%m%d).sql

# Сжатый дамп
pg_dump -U postgres -d warehouse_db | gzip > backup_$(date +%Y%m%d).sql.gz
```

### Восстановление из резервной копии

```bash
# Восстановление из дампа
psql -U postgres -d warehouse_db < backup_20241201.sql

# Восстановление из сжатого дампа
gunzip -c backup_20241201.sql.gz | psql -U postgres -d warehouse_db
```

### Настройка автоматического резервного копирования (cron)

```bash
# Добавить в crontab (ежедневно в 2:00)
0 2 * * * pg_dump -U postgres -d warehouse_db > /backups/warehouse_$(date +\%Y\%m\%d).sql
```

## Обновление системы

### Обновление бэкенда

```bash
# Остановка приложения
# Скачивание новой версии
git pull origin main

# Пересборка
mvn clean package

# Запуск
java -jar target/warehouse-management-api-1.0.0.jar
```

### Обновление фронтенда

```bash
cd warehouse-frontend
git pull origin main
npm install
ng build --prod
```