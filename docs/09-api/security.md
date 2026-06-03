### Безопасность

#### JWT-аутентификация
![alt text](/docs/09-api/images/jwt.png)

## Что такое JWT

JWT (JSON Web Token) — это компактный и самодостаточный способ передачи информации между сторонами в виде JSON-объекта. Он используется для аутентификации и авторизации.

## Структура JWT

```text
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJmdWxsTmFtZSI6IuCQk-CRg-CQviIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzA1MjYwMDAwLCJleHAiOjE3MDUzNDY0MDB9.signature
```

JWT состоит из трёх частей, разделённых точками:

| Часть | Описание |
|---------|---------|
| Header | Заголовок, содержащий алгоритм шифрования (HS512) и тип токена (JWT) |
| Payload | Полезная нагрузка — данные о пользователе (email, userId, fullName, role) |
| Signature | Цифровая подпись для верификации токена |

### Данные в Payload

| Поле | Описание |
|---------|---------|
| sub (subject) | Email пользователя |
| userId | ID пользователя в БД |
| fullName | Полное имя пользователя |
| role | Роль пользователя (USER / MANAGER) |
| iat (issued at) | Время выдачи токена |
| exp (expiration) | Время истечения токена |

## Конфигурация JWT

### Настройки в application.properties

```properties
# JWT Configuration
app.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
app.jwt.expiration=86400000
```

| Параметр | Значение | Описание |
|-----------|-----------|-----------|
| app.jwt.secret | 512-битный секретный ключ | Используется для подписи токенов |
| app.jwt.expiration | 86400000 (24 часа) | Время жизни токена в миллисекундах |

### Генерация токена (JwtTokenProvider)

```java
public String generateToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpiration);

    return Jwts.builder()
            .setSubject(userPrincipal.getEmail())
            .claim("userId", userPrincipal.getId())
            .claim("fullName", userPrincipal.getFullName())
            .claim("role", userPrincipal.getRole())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key(), SignatureAlgorithm.HS512)
            .compact();
}
```

### Валидация токена

```java
public boolean validateToken(String token) {
    try {
        Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
        return true;
    } catch (JwtException | IllegalArgumentException e) {
        return false;
    }
}
```

## Ролевая модель

### Роли пользователей

| Роль | Описание | Права |
|--------|--------|--------|
| USER | Обычный пользователь | Только просмотр данных |
| MANAGER | Менеджер склада | Полный доступ ко всем операциям |

### Матрица доступа

| Операция | USER | MANAGER |
|-----------|------|----------|
| Просмотр сводки по складу (GET /api/inventory/summary) | ✅ | ✅ |
| Просмотр остатков товара (GET /api/inventory/product/{id}/locations) | ✅ | ✅ |
| Получение списка товаров (GET /api/products) | ✅ | ✅ |
| Получение списка локаций (GET /api/locations) | ✅ | ✅ |
| Массовая приёмка (POST /api/movements/receipt/batch) | ❌ | ✅ |
| Массовая отгрузка (POST /api/movements/shipment/batch) | ❌ | ✅ |
| Перемещение товара (POST /api/movements/move) | ❌ | ✅ |

## Реализация на бэкенде

### SecurityConfig.java

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Публичные эндпоинты
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()

                // Просмотр данных (USER и MANAGER)
                .requestMatchers("/api/inventory/**").authenticated()
                .requestMatchers("/api/products/**").authenticated()
                .requestMatchers("/api/locations/**").authenticated()

                // Операции изменения (только MANAGER)
                .requestMatchers("/api/movements/**").hasRole("MANAGER")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### Получение текущего пользователя из контекста

```java
@GetMapping("/me")
public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    UserDto userDto = new UserDto(
        userDetails.getId(),
        userDetails.getEmail(),
        userDetails.getFullName(),
        userDetails.getRole()
    );

    return ResponseEntity.ok(userDto);
}
```

## Реализация на фронтенде (Angular)

### Хранение токена

```typescript
// StorageService
@Injectable({ providedIn: 'root' })
export class StorageService {
    private readonly TOKEN_KEY = 'access_token';
    private readonly USER_KEY = 'user_data';

    setToken(token: string): void {
        localStorage.setItem(this.TOKEN_KEY, token);
    }

    getToken(): string | null {
        return localStorage.getItem(this.TOKEN_KEY);
    }

    setUser(user: any): void {
        localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    }

    getUser(): any {
        const user = localStorage.getItem(this.USER_KEY);
        return user ? JSON.parse(user) : null;
    }

    clear(): void {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
    }
}
```

### Перехватчик для добавления токена (AuthInterceptor)

```typescript
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    private storage = inject(StorageService);

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const token = this.storage.getToken();

        if (token) {
            const cloned = req.clone({
                headers: req.headers.set('Authorization', `Bearer ${token}`)
            });

            return next.handle(cloned);
        }

        return next.handle(req);
    }
}
```

### Guard для защиты маршрутов (AuthGuard)

```typescript
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
    private storage = inject(StorageService);
    private router = inject(Router);
    private roleService = inject(RoleService);

    canActivate(route: ActivatedRouteSnapshot): boolean {
        const token = this.storage.getToken();
        const requiredRole = route.data['role'];

        if (!token) {
            this.router.navigate(['/login']);
            return false;
        }

        if (requiredRole === 'MANAGER' && !this.roleService.isManager()) {
            this.router.navigate(['/inventory/summary']);
            return false;
        }

        return true;
    }
}
```

### Сервис для проверки ролей (RoleService)

```typescript
@Injectable({ providedIn: 'root' })
export class RoleService {
    private storage = inject(StorageService);

    isManager(): boolean {
        const user = this.storage.getUser();
        return user?.role === 'MANAGER';
    }

    isUser(): boolean {
        const user = this.storage.getUser();
        return user?.role === 'USER';
    }

    getCurrentRole(): string | null {
        const user = this.storage.getUser();
        return user?.role || null;
    }
}
```

### Использование в шаблоне (скрытие элементов по роли)

```html
<!-- Навигация для MANAGER -->
@if (roleService.isManager()) {
  <a routerLink="/movements/receipt">Приёмка</a>
  <a routerLink="/movements/shipment">Отгрузка</a>
  <a routerLink="/movements/transfer">Перемещение</a>
}
```

## Обработка ошибок аутентификации

### Типичные ошибки

| HTTP код | Описание | Действие на клиенте |
|-----------|-----------|---------------------|
| 401 Unauthorized | Отсутствует или недействительный токен | Очистить localStorage, перенаправить на /login |
| 403 Forbidden | Недостаточно прав для операции | Показать сообщение "Недостаточно прав" |

### Перехватчик для обработки ошибок (ErrorInterceptor)

```typescript
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    private storage = inject(StorageService);
    private router = inject(Router);

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(req).pipe(
            catchError((error: HttpErrorResponse) => {
                if (error.status === 401) {
                    this.storage.clear();
                    this.router.navigate(['/login']);
                }
                return throwError(() => error);
            })
        );
    }
}
```

## Безопасность паролей

### Хеширование паролей (BCrypt)

```java
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // ...
    }
}
```

### Настройка PasswordEncoder

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```