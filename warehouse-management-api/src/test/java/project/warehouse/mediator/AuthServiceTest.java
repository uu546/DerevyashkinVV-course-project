package project.warehouse.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.warehouse.control.dto.AuthRequest;
import project.warehouse.control.dto.AuthResponse;
import project.warehouse.control.dto.RegisterRequest;
import project.warehouse.entity.User;
import project.warehouse.foundation.interfaces.IUserRepository;
import project.warehouse.mediator.services.AuthService;
import project.warehouse.security.JwtTokenProvider;
import project.warehouse.security.UserDetailsImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserDetailsImpl userDetails;
    private AuthRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        // Создаём тестового пользователя
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encoded_password");
        testUser.setFullName("Тестовый Пользователь");
        testUser.setRole("USER");
        testUser.setIsActive(true);

        // Создаём UserDetails
        userDetails = UserDetailsImpl.build(testUser);

        // Создаём запрос на логин
        loginRequest = new AuthRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Создаём запрос на регистрацию
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("newpassword123");
        registerRequest.setFullName("Новый Пользователь");
    }

    // ==================== ТЕСТЫ login ====================

    @Test
    void testLogin_Success_ShouldReturnAuthResponse() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt_token_123");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt_token_123", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Тестовый Пользователь", response.getFullName());
        assertEquals("USER", response.getRole());
        assertEquals(3600000L, response.getExpiresIn());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authentication);
    }

    @Test
    void testLogin_WithInvalidCredentials_ShouldThrowException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }

    @Test
    void testLogin_WithNullEmail_ShouldThrowException() {
        // Arrange
        loginRequest.setEmail(null);

        // Act & Assert
        assertThrows(Exception.class, () -> authService.login(loginRequest));
    }

    @Test
    void testLogin_WithNullPassword_ShouldThrowException() {
        // Arrange
        loginRequest.setPassword(null);

        // Act & Assert
        assertThrows(Exception.class, () -> authService.login(loginRequest));
    }

    @Test
    void testLogin_WithEmptyEmail_ShouldThrowException() {
        // Arrange
        loginRequest.setEmail("");

        // Act & Assert
        assertThrows(Exception.class, () -> authService.login(loginRequest));
    }

    @Test
    void testLogin_WithEmptyPassword_ShouldThrowException() {
        // Arrange
        loginRequest.setPassword("");

        // Act & Assert
        assertThrows(Exception.class, () -> authService.login(loginRequest));
    }

    // ==================== ТЕСТЫ register ====================

    @Test
    void testRegister_Success_ShouldCreateUserAndReturnAuthResponse() {
        // Arrange
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword123")).thenReturn("encoded_new_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2);
            return savedUser;
        });

        // Mock для автоматического логина
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt_token_registration");

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt_token_registration", response.getAccessToken());

        verify(userRepository).existsByEmail("newuser@example.com");
        verify(passwordEncoder).encode("newpassword123");
        verify(userRepository).save(any(User.class));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testRegister_WithExistingEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });
        assertEquals("Email already exists: newuser@example.com", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_UserHasDefaultRole_ShouldSetUserRole() {
        // Arrange
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword123")).thenReturn("encoded_new_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2);
            return savedUser;
        });

        // Mock для автоматического логина
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt_token");

        // Act
        authService.register(registerRequest);

        // Assert - проверяем что пользователь создан с ролью USER
        verify(userRepository).save(argThat(user ->
                "USER".equals(user.getRole()) &&
                        user.getIsActive() == true
        ));
    }

    @Test
    void testRegister_UserHasDefaultActiveStatus_ShouldSetIsActiveTrue() {
        // Arrange
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword123")).thenReturn("encoded_new_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2);
            return savedUser;
        });

        // Mock для автоматического логина
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt_token");

        // Act
        authService.register(registerRequest);

        // Assert
        verify(userRepository).save(argThat(user -> user.getIsActive() == true));
    }

    @Test
    void testRegister_WithNullEmail_ShouldThrowException() {
        // Arrange
        registerRequest.setEmail(null);

        // Act & Assert
        assertThrows(Exception.class, () -> authService.register(registerRequest));
    }

    @Test
    void testRegister_WithEmptyEmail_ShouldThrowException() {
        // Arrange
        registerRequest.setEmail("");

        // Act & Assert
        assertThrows(Exception.class, () -> authService.register(registerRequest));
    }

    @Test
    void testRegister_WithNullPassword_ShouldThrowException() {
        // Arrange
        registerRequest.setPassword(null);

        // Act & Assert
        assertThrows(Exception.class, () -> authService.register(registerRequest));
    }

    @Test
    void testRegister_WithEmptyPassword_ShouldThrowException() {
        // Arrange
        registerRequest.setPassword("");

        // Act & Assert
        assertThrows(Exception.class, () -> authService.register(registerRequest));
    }

    @Test
    void testRegister_WithNullFullName_ShouldStillCreateUser() {
        // Arrange
        registerRequest.setFullName(null);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword123")).thenReturn("encoded_new_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2);
            return savedUser;
        });

        // Mock для автоматического логина
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt_token");

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        verify(userRepository).save(any(User.class));
    }

    // ==================== ТЕСТЫ интеграции login и register ====================

    @Test
    void testRegister_ThenLogin_ShouldWork() {
        // Arrange
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword123")).thenReturn("encoded_new_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2);
            return savedUser;
        });

        // Mock для автоматического логина
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt_token");

        // Act - регистрация
        AuthResponse registerResponse = authService.register(registerRequest);

        // Assert
        assertNotNull(registerResponse);

        // Создаём запрос на логин с теми же данными
        AuthRequest loginWithNewUser = new AuthRequest();
        loginWithNewUser.setEmail("newuser@example.com");
        loginWithNewUser.setPassword("newpassword123");

        // Act - логин (мокаем заново для второго вызова)
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("another_jwt_token");

        AuthResponse loginResponse = authService.login(loginWithNewUser);

        // Assert
        assertNotNull(loginResponse);
        assertEquals("another_jwt_token", loginResponse.getAccessToken());
    }

    // ==================== ТЕСТЫ на граничные случаи ====================

    @Test
    void testLogin_UserWithManagerRole_ShouldReturnManagerRole() {
        // Arrange
        User managerUser = new User();
        managerUser.setId(2);
        managerUser.setEmail("manager@example.com");
        managerUser.setPassword("encoded_manager_password");
        managerUser.setFullName("Менеджер");
        managerUser.setRole("MANAGER");
        managerUser.setIsActive(true);

        UserDetailsImpl managerDetails = UserDetailsImpl.build(managerUser);

        AuthRequest managerLoginRequest = new AuthRequest();
        managerLoginRequest.setEmail("manager@example.com");
        managerLoginRequest.setPassword("manager123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(managerDetails);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt_token_manager");

        // Act
        AuthResponse response = authService.login(managerLoginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("MANAGER", response.getRole());
    }

    @Test
    void testRegister_PasswordEncoding_CalledWithCorrectPassword() {
        // Arrange
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword123")).thenReturn("encoded_new_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock для автоматического логина
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt_token");

        // Act
        authService.register(registerRequest);

        // Assert
        verify(passwordEncoder).encode("newpassword123");
    }
}