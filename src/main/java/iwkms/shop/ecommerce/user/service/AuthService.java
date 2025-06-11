package iwkms.shop.ecommerce.user.service;

import iwkms.shop.ecommerce.user.dto.LoginDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public String login(LoginDto loginDto) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDto.email(), 
                loginDto.password()
                )
        );
        var userDetails = userDetailsService.loadUserByUsername(loginDto.email());

        return jwtService.generateToken(userDetails);
    }
}
