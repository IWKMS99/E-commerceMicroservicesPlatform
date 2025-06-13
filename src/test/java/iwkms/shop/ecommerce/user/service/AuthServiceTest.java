package iwkms.shop.ecommerce.user.service;

import iwkms.shop.ecommerce.user.dto.LoginDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_withValidCredentials_shouldReturnJwtToken() {
        LoginDto loginDto = new LoginDto("test@example.com", "password");
        UserDetails userDetails = new User(loginDto.email(), loginDto.password(), Collections.emptyList());
        String expectedToken = "jwt-token-string";

        when(userDetailsService.loadUserByUsername(loginDto.email())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(expectedToken);

        String actualToken = authService.login(loginDto);

        assertEquals(expectedToken, actualToken);
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password())
        );
    }

    @Test
    void login_withInvalidCredentials_shouldThrowException() {
        LoginDto loginDto = new LoginDto("test@example.com", "wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginDto);
        });

        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }
}