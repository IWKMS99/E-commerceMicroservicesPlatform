package iwkms.shop.ecommerce.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import iwkms.shop.ecommerce.shared.exception.UserAlreadyExistsException;
import iwkms.shop.ecommerce.user.dto.RegisterUserDto;
import iwkms.shop.ecommerce.user.entity.User;
import iwkms.shop.ecommerce.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void registerUser_shouldReturnCreated_whenRegistrationIsSuccessful() throws Exception {
        RegisterUserDto registerDto = new RegisterUserDto("new.user@example.com", "password123", "New", "User");
        User createdUser = new User();
        createdUser.setId(UUID.randomUUID());
        when(userService.registerUser(any(RegisterUserDto.class))).thenReturn(createdUser);

        mockMvc.perform(post("/api/v1/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User created successfully with ID: " + createdUser.getId()));
    }

    @Test
    void registerUser_shouldReturnConflict_whenUserAlreadyExists() throws Exception {
        RegisterUserDto registerDto = new RegisterUserDto("existing.user@example.com", "password123", "Existing", "User");
        when(userService.registerUser(any(RegisterUserDto.class)))
                .thenThrow(new UserAlreadyExistsException("User with this email already exists"));

        mockMvc.perform(post("/api/v1/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "test.user@example.com")
    void getCurrentUser_shouldReturnUsername_whenUserIsAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, test.user@example.com"));
    }

    @Test
    void getCurrentUser_shouldReturnUnauthorized_whenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}