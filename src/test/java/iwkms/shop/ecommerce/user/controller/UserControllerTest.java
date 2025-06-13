package iwkms.shop.ecommerce.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import iwkms.shop.ecommerce.config.ApplicationConfig;
import iwkms.shop.ecommerce.config.SecurityConfig;
import iwkms.shop.ecommerce.shared.exception.UserAlreadyExistsException;
import iwkms.shop.ecommerce.user.dto.RegisterUserDto;
import iwkms.shop.ecommerce.user.entity.User;
import iwkms.shop.ecommerce.user.repository.UserRepository;
import iwkms.shop.ecommerce.user.service.UserService;
import iwkms.shop.ecommerce.user.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, ApplicationConfig.class}) // Импортируем наши конфиги для полной Security
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Мокаем зависимости ---
    @MockBean
    private UserService userService;

    // Зависимости, которые нужны для импортированных конфигов
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private iwkms.shop.ecommerce.config.CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    // --- Тесты для /register ---

    @Test
    void registerUser_shouldReturnCreated_whenRegistrationIsSuccessful() throws Exception {
        // Arrange
        RegisterUserDto registerDto = new RegisterUserDto("new.user@example.com", "password123", "New", "User");
        User createdUser = new User();
        createdUser.setId(UUID.randomUUID());

        when(userService.registerUser(any(RegisterUserDto.class))).thenReturn(createdUser);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User created successfully with ID: " + createdUser.getId()));
    }

    @Test
    void registerUser_shouldReturnConflict_whenUserAlreadyExists() throws Exception {
        // Arrange
        RegisterUserDto registerDto = new RegisterUserDto("existing.user@example.com", "password123", "Existing", "User");

        // Настраиваем мок так, чтобы он бросал исключение, которое вы используете для дубликатов
        when(userService.registerUser(any(RegisterUserDto.class)))
                .thenThrow(new UserAlreadyExistsException("User with email " + registerDto.email() + " already exists."));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isConflict()); // Ожидаем 409 Conflict (согласно вашему GlobalExceptionHandler)
    }

    @Test
    void registerUser_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        // Arrange
        RegisterUserDto registerDto = new RegisterUserDto("invalid-email", "password123", "Test", "User");

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest()); // Ожидаем 400 из-за валидации
    }

    // --- Тесты для /me ---

    @Test
    @WithMockUser(username = "test.user@example.com") // Имитируем аутентифицированного пользователя
    void getCurrentUser_shouldReturnUsername_whenUserIsAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/users/me")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, test.user@example.com"));
    }

    @Test
    void getCurrentUser_shouldReturnUnauthorized_whenUserIsNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/users/me")
                        .with(csrf()))
                .andExpect(status().isUnauthorized()); // Ожидаем 401, т.к. нет @WithMockUser
    }
}