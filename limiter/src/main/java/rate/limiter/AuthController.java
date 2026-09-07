package rate.limiter;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rate.limiter.models.LoginDto;
import rate.limiter.models.RegisterDto;
import rate.limiter.models.User;
import rate.limiter.models.UserResponseDto;
import rate.limiter.repositories.UserRepository;
import rate.limiter.security.jwt.TokenService;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        if(userRepository.existsByUsername(registerDto.username()) || userRepository.existsByEmail(registerDto.email())) {
            return ResponseEntity.badRequest().build();
        }
        User newUser = new User();
        newUser.setUsername(registerDto.username());
        newUser.setEmail(registerDto.email());
        newUser.setPassword(passwordEncoder.encode(registerDto.password()));
        newUser.setRoles(List.of("ROLE_USER"));
        userRepository.save(newUser);

        UserResponseDto response = new UserResponseDto(
                newUser.getUsername(),
                newUser.getRoles()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        User user = userRepository.findByEmail(loginDto.email())
                .orElseThrow(() -> new RuntimeException());

        if(!passwordEncoder.matches(loginDto.password(), user.getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        ResponseCookie cookie = tokenService.createCookie(tokenService.generateToken(user));
        UserResponseDto responseDto = new UserResponseDto(
                user.getUsername(),
                user.getRoles()
        );
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(responseDto);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        tokenService.logout();
        return ResponseEntity.ok().build();
    }


}
