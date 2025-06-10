package iwkms.shop.ecommerce.user.controller;

import iwkms.shop.ecommerce.user.dto.RegisterUserDto;
import iwkms.shop.ecommerce.user.entity.User;
import iwkms.shop.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterUserDto registerDto) {
        User newUser = userService.registerUser(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created successfully with ID: " + newUser.getId());
    }
}