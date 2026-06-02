package project.warehouse.foundation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import project.warehouse.entity.User;
import project.warehouse.foundation.interfaces.IUserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UserRepositoryTest {

    @Autowired
    private IUserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Создаём тестового пользователя
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("encoded_password_123");
        testUser.setFullName("Тестовый Пользователь");
        testUser.setRole("USER");
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        entityManager.persist(testUser);

        entityManager.flush();
    }

    // ==================== ТЕСТЫ findById ====================

    @Test
    void testFindById_ShouldReturnUser() {
        Optional<User> found = userRepository.findById(testUser.getId());

        assertTrue(found.isPresent());
        assertEquals(testUser.getId(), found.get().getId());
        assertEquals("test@example.com", found.get().getEmail());
        assertEquals("Тестовый Пользователь", found.get().getFullName());
        assertEquals("USER", found.get().getRole());
        assertTrue(found.get().getIsActive());
    }

    @Test
    void testFindById_WithNullId_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findById(null);

        assertFalse(found.isPresent());
    }

    @Test
    void testFindById_NotFound_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findById(999);

        assertFalse(found.isPresent());
    }

    @Test
    void testFindById_WithNegativeId_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findById(-1);

        assertFalse(found.isPresent());
    }

    @Test
    void testFindById_WithZeroId_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findById(0);

        assertFalse(found.isPresent());
    }

    // ==================== ТЕСТЫ findByEmail ====================

    @Test
    void testFindByEmail_ShouldReturnUser() {
        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals(testUser.getId(), found.get().getId());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmail_CaseSensitive_ShouldMatch() {
        Optional<User> found = userRepository.findByEmail("TEST@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByEmail_NotFound_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByEmail_WithNullEmail_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findByEmail(null);

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByEmail_WithEmptyString_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByEmail_WithBlankString_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("   ");

        assertFalse(found.isPresent());
    }

    // ==================== ТЕСТЫ existsByEmail ====================

    @Test
    void testExistsByEmail_WithExistingEmail_ShouldReturnTrue() {
        boolean exists = userRepository.existsByEmail("test@example.com");

        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_WithNonExistingEmail_ShouldReturnFalse() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        assertFalse(exists);
    }

    @Test
    void testExistsByEmail_WithNullEmail_ShouldReturnFalse() {
        boolean exists = userRepository.existsByEmail(null);

        assertFalse(exists);
    }

    @Test
    void testExistsByEmail_WithEmptyString_ShouldReturnFalse() {
        boolean exists = userRepository.existsByEmail("");

        assertFalse(exists);
    }

    @Test
    void testExistsByEmail_WithBlankString_ShouldReturnFalse() {
        boolean exists = userRepository.existsByEmail("   ");

        assertFalse(exists);
    }

    @Test
    void testExistsByEmail_CaseSensitive_ShouldReturnFalse() {
        boolean exists = userRepository.existsByEmail("TEST@example.com");

        assertFalse(exists);
    }

    // ==================== ТЕСТЫ save ====================

    @Test
    void testSave_NewUser_ShouldPersist() {
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("encoded_password_456");
        newUser.setFullName("Новый Пользователь");
        newUser.setRole("MANAGER");
        newUser.setIsActive(true);

        User saved = userRepository.save(newUser);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("newuser@example.com", saved.getEmail());
        assertEquals("Новый Пользователь", saved.getFullName());
        assertEquals("MANAGER", saved.getRole());
        assertTrue(saved.getIsActive());

        // Проверяем, что запись действительно сохранилась в БД
        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("newuser@example.com", found.get().getEmail());
    }

    @Test
    void testSave_NewUserWithDefaultValues_ShouldPersist() {
        User newUser = new User();
        newUser.setEmail("default@example.com");
        newUser.setPassword("encoded_password_789");
        newUser.setFullName("Пользователь по умолчанию");
        // role не установлена, должна быть "USER" по умолчанию из entity
        // isActive не установлен, должен быть true по умолчанию

        User saved = userRepository.save(newUser);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("USER", saved.getRole());
        assertTrue(saved.getIsActive());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testSave_NewUserWithInactive_ShouldPersist() {
        User inactiveUser = new User();
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setPassword("encoded_password_000");
        inactiveUser.setFullName("Неактивный пользователь");
        inactiveUser.setRole("USER");
        inactiveUser.setIsActive(false);

        User saved = userRepository.save(inactiveUser);

        assertNotNull(saved);
        assertFalse(saved.getIsActive());

        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertFalse(found.get().getIsActive());
    }

    @Test
    void testSave_NewUserWithManagerRole_ShouldPersist() {
        User managerUser = new User();
        managerUser.setEmail("manager@example.com");
        managerUser.setPassword("encoded_password_mgr");
        managerUser.setFullName("Менеджер склада");
        managerUser.setRole("MANAGER");
        managerUser.setIsActive(true);

        User saved = userRepository.save(managerUser);

        assertNotNull(saved);
        assertEquals("MANAGER", saved.getRole());

        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("MANAGER", found.get().getRole());
    }

    @Test
    void testSave_UpdateExistingUser_ShouldMerge() {
        // Обновляем существующего пользователя
        testUser.setFullName("Обновлённое Имя");
        testUser.setRole("MANAGER");
        testUser.setIsActive(false);

        User updated = userRepository.save(testUser);

        assertNotNull(updated);
        assertEquals("Обновлённое Имя", updated.getFullName());
        assertEquals("MANAGER", updated.getRole());
        assertFalse(updated.getIsActive());

        // Проверяем, что изменения сохранились в БД
        Optional<User> found = userRepository.findById(testUser.getId());
        assertTrue(found.isPresent());
        assertEquals("Обновлённое Имя", found.get().getFullName());
        assertEquals("MANAGER", found.get().getRole());
        assertFalse(found.get().getIsActive());
    }

    @Test
    void testSave_UpdateUserEmail_ShouldMerge() {
        testUser.setEmail("updated@example.com");

        User updated = userRepository.save(testUser);

        assertNotNull(updated);
        assertEquals("updated@example.com", updated.getEmail());

        Optional<User> found = userRepository.findById(testUser.getId());
        assertTrue(found.isPresent());
        assertEquals("updated@example.com", found.get().getEmail());

        // Старый email больше не должен существовать
        assertFalse(userRepository.existsByEmail("test@example.com"));
        assertTrue(userRepository.existsByEmail("updated@example.com"));
    }

    @Test
    void testSave_UpdateUserPassword_ShouldMerge() {
        testUser.setPassword("new_encoded_password");

        User updated = userRepository.save(testUser);

        assertNotNull(updated);
        assertEquals("new_encoded_password", updated.getPassword());

        Optional<User> found = userRepository.findById(testUser.getId());
        assertTrue(found.isPresent());
        assertEquals("new_encoded_password", found.get().getPassword());
    }

    // ==================== ТЕСТЫ existsByEmail после save ====================

    @Test
    void testExistsByEmail_AfterSaveNewUser_ShouldReturnTrue() {
        String newEmail = "postsave@example.com";
        assertFalse(userRepository.existsByEmail(newEmail));

        User newUser = new User();
        newUser.setEmail(newEmail);
        newUser.setPassword("password");
        newUser.setFullName("После сохранения");
        userRepository.save(newUser);

        assertTrue(userRepository.existsByEmail(newEmail));
    }

    @Test
    void testExistsByEmail_AfterUpdateEmail_ShouldReturnCorrectResults() {
        String oldEmail = "test@example.com";
        String newEmail = "updated_email@example.com";

        assertTrue(userRepository.existsByEmail(oldEmail));
        assertFalse(userRepository.existsByEmail(newEmail));

        testUser.setEmail(newEmail);
        userRepository.save(testUser);

        assertFalse(userRepository.existsByEmail(oldEmail));
        assertTrue(userRepository.existsByEmail(newEmail));
    }

    // ==================== ТЕСТЫ findById после операций ====================

    @Test
    void testFindById_AfterSave_ShouldReturnNewUser() {
        User newUser = new User();
        newUser.setEmail("findafter@example.com");
        newUser.setPassword("password");
        newUser.setFullName("Поиск после сохранения");

        User saved = userRepository.save(newUser);

        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getEmail(), found.get().getEmail());
    }

    @Test
    void testFindById_AfterUpdate_ShouldReturnUpdatedUser() {
        String newName = "Обновлённое имя пользователя";
        testUser.setFullName(newName);
        userRepository.save(testUser);

        Optional<User> found = userRepository.findById(testUser.getId());
        assertTrue(found.isPresent());
        assertEquals(newName, found.get().getFullName());
    }

    // ==================== ТЕСТЫ findByEmail после операций ====================

    @Test
    void testFindByEmail_AfterSave_ShouldReturnNewUser() {
        String newEmail = "findbyemail@example.com";
        User newUser = new User();
        newUser.setEmail(newEmail);
        newUser.setPassword("password");
        newUser.setFullName("Поиск по email");

        userRepository.save(newUser);

        Optional<User> found = userRepository.findByEmail(newEmail);
        assertTrue(found.isPresent());
        assertEquals(newEmail, found.get().getEmail());
    }

    @Test
    void testFindByEmail_AfterUpdate_ShouldReturnUpdatedUser() {
        String newEmail = "newemailforfind@example.com";
        testUser.setEmail(newEmail);
        userRepository.save(testUser);

        Optional<User> foundByNew = userRepository.findByEmail(newEmail);
        Optional<User> foundByOld = userRepository.findByEmail("test@example.com");

        assertTrue(foundByNew.isPresent());
        assertFalse(foundByOld.isPresent());
    }

    // ==================== ТЕСТЫ граничных случаев ====================

    @Test
    void testSave_UserWithNullEmail_ShouldThrowException() {
        User invalidUser = new User();
        invalidUser.setEmail(null);
        invalidUser.setPassword("password");
        invalidUser.setFullName("Без email");

        assertThrows(Exception.class, () -> {
            userRepository.save(invalidUser);
            entityManager.flush();
        });
    }

    @Test
    void testSave_UserWithNullPassword_ShouldThrowException() {
        User invalidUser = new User();
        invalidUser.setEmail("nopassword@example.com");
        invalidUser.setPassword(null);
        invalidUser.setFullName("Без пароля");

        assertThrows(Exception.class, () -> {
            userRepository.save(invalidUser);
            entityManager.flush();
        });
    }

    @Test
    void testSave_UserWithDuplicateEmail_ShouldThrowException() {
        User duplicateUser = new User();
        duplicateUser.setEmail("test@example.com"); // email уже существует
        duplicateUser.setPassword("another_password");
        duplicateUser.setFullName("Дубликат");

        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateUser);
            entityManager.flush();
        });
    }

    // ==================== ТЕСТЫ save с повторным вызовом ====================

    @Test
    void testSave_Twice_ShouldWorkCorrectly() {
        User user = new User();
        user.setEmail("twice@example.com");
        user.setPassword("password");
        user.setFullName("Двойное сохранение");

        User saved1 = userRepository.save(user);
        assertNotNull(saved1.getId());

        saved1.setFullName("Изменённое имя");
        User saved2 = userRepository.save(saved1);

        assertEquals(saved1.getId(), saved2.getId());
        assertEquals("Изменённое имя", saved2.getFullName());

        Optional<User> found = userRepository.findById(saved1.getId());
        assertTrue(found.isPresent());
        assertEquals("Изменённое имя", found.get().getFullName());
    }
}