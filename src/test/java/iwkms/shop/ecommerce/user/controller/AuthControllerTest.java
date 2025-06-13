package iwkms.shop.ecommerce.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import iwkms.shop.ecommerce.config.ApplicationConfig;
import iwkms.shop.ecommerce.config.SecurityConfig;
import iwkms.shop.ecommerce.shared.exception.GlobalExceptionHandler;
import iwkms.shop.ecommerce.user.dto.LoginDto;
import iwkms.shop.ecommerce.user.repository.UserRepository;
import iwkms.shop.ecommerce.user.service.AuthService;
import iwkms.shop.ecommerce.user.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, ApplicationConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private iwkms.shop.ecommerce.config.CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        LoginDto loginDto = new LoginDto("test@example.com", "password123");
        String fakeJwtToken = "fake-jwt-token";
        when(authService.login(loginDto)).thenReturn(fakeJwtToken);

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(fakeJwtToken));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {
        LoginDto loginDto = new LoginDto("wrong@example.com", "wrongpassword");
        when(authService.login(loginDto)).thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
        LoginDto loginDto = new LoginDto("not-an-email", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnBadRequest_WhenPasswordIsBlank() throws Exception {
        LoginDto loginDto = new LoginDto("test@example.com", "");

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }
}