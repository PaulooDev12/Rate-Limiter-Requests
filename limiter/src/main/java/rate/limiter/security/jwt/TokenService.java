package rate.limiter.security.jwt;

import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import rate.limiter.models.User;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private final JwtEncoder jwtEncoder;
    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }
    public String generateToken(User user) {
        Instant now = Instant.now();
        long expiration = 3600L;
        String scope = String.join(" ", user.getRoles());
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("my api")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiration))
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("scope", scope)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
    public ResponseCookie createCookie(String token) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("strict")
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();
    }
    public ResponseCookie logout() {
        return ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("strict")
                .path("/")
                .maxAge(0)
                .build();
    }
}
