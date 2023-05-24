package eu.senla.userservice.token.jwt;

import eu.senla.userservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtProvider {

    private final JwtSecurityProperties securityProperties;

    @Override
    public String generateAccessToken(User user) {
        log.trace("Method generateAccessToken");
        Date date = Date.from(LocalDateTime.now()
                .plusMinutes(securityProperties.getAccessTime().toMinutes())
                .atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, securityProperties.getAccessSecret())
                .claim("role", user.getRole())
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {
        log.trace("Method generateRefreshToken");
        Date date = Date.from(LocalDateTime.now()
                .plusDays(securityProperties.getRefreshTime().getDays())
                .atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, securityProperties.getRefreshSecret())
                .compact();
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, securityProperties.getAccessSecret());
    }

    @Override
    public String getLoginFromAccessToken(String token) {
        log.trace("Method getLoginFromAccessToken");
        return getLoginFromToken(token, securityProperties.getAccessSecret());
    }

    private boolean validateToken(String token, String secret) {
        log.trace("Method validateToken");
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            log.trace("Return true");
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.trace("Return false");
        return false;
    }

    private String getLoginFromToken(String token, String secret) {
        log.trace("Method getLoginFromToken");
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}