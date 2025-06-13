package iwkms.shop.ecommerce.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        String secretKey = "NGRmOTI3M2QyYjY2ZjY2NjYxZTE4MjY3YjlmYjE4ZGMxZjJkZDQ0YjdiZjM3OTFlN2M5Mjg3ZDYyYmEyMDIxZQ==";
        long expiration = 86400000; // 24 часа

        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", expiration);
    }

    @Test
    void generateToken_and_extractUsername_shouldWorkCorrectly() {
        UserDetails userDetails = new User("test@example.com", "password", Collections.emptyList());

        String token = jwtService.generateToken(userDetails);
        String extractedUsername = jwtService.extractUsername(token);

        assertNotNull(token);
        assertEquals("test@example.com", extractedUsername);
    }

    @Test
    void isTokenValid_withValidTokenAndCorrectUser_shouldReturnTrue() {
        UserDetails userDetails = new User("test@example.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_withValidTokenAndIncorrectUser_shouldReturnFalse() {
        UserDetails userDetails1 = new User("user1@example.com", "p", Collections.emptyList());
        UserDetails userDetails2 = new User("user2@example.com", "p", Collections.emptyList());
        String token = jwtService.generateToken(userDetails1);

        boolean isValid = jwtService.isTokenValid(token, userDetails2);

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_withExpiredToken_shouldReturnFalse() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L);
        UserDetails userDetails = new User("test@example.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        Thread.sleep(5);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertFalse(isValid);
    }
}