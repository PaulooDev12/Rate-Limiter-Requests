package rate.limiter.security.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import rate.limiter.models.UserPrincipal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JwtToUserConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String scopeClaim = jwt.getClaimAsString("scope");
        Collection<GrantedAuthority> authorities = (scopeClaim == null || scopeClaim.isBlank())
                ? Collections.emptyList()
                : Arrays.stream(scopeClaim.split(" "))
                .<GrantedAuthority>map(SimpleGrantedAuthority::new)
                .toList();

        String email = jwt.getClaimAsString("email") != null
                ? jwt.getClaimAsString("email")
                : jwt.getSubject();

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        UserPrincipal userPrincipal = new UserPrincipal(
                jwt.getSubject(),
                email,
                roles
        );
        return new UsernamePasswordAuthenticationToken(userPrincipal, jwt, authorities);
    }
}
