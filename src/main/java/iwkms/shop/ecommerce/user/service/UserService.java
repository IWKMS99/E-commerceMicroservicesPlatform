package iwkms.shop.ecommerce.user.service;

import iwkms.shop.ecommerce.user.dto.RegisterUserDto;
import iwkms.shop.ecommerce.user.entity.User;
import iwkms.shop.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(RegisterUserDto registerDto) {
        if (userRepository.findByEmail(registerDto.email()).isPresent()) {
            throw new IllegalStateException("Email already taken");
        }

        User user = new User();
        user.setEmail(registerDto.email());
        user.setPassword(passwordEncoder.encode(registerDto.password()));
        user.setFirstName(registerDto.firstName());
        user.setLastName(registerDto.lastName());

        return userRepository.save(user);
    }
}