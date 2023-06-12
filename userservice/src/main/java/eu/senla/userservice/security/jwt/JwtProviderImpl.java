package eu.senla.userservice.security.jwt;

import eu.senla.userservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtProvider {

    private final JwtSecurityProperties securityProperties;

    @Override
    public String generateAccessToken(@NotNull User user) {
        log.info("Method generateAccessToken");
        Date date = Date.from(LocalDateTime.now()
                .plusDays(securityProperties.getAccessTime().getDays())
                .atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, securityProperties.getAccessSecret())
                .claim("role", user.getRole())
                .compact();
    }

    @Override
    public String generateRefreshToken(@NotNull User user) {
        log.info("Method generateRefreshToken");
        Date date = Date.from(LocalDateTime.now()
                .plusDays(securityProperties.getRefreshTime().getDays())
                .atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, securityProperties.getRefreshSecret())
                .compact();
    }

    @Override
    public boolean validateAccessToken(@NotNull String accessToken) {
        log.info("Method validateAccessToken");
        return validateToken(accessToken, securityProperties.getAccessSecret());
    }

    @Override
    public boolean validateRefreshToken(@NotNull String refreshToken) {
        log.info("Method validateRefreshToken");
        return validateToken(refreshToken, securityProperties.getRefreshSecret());
    }

    @Override
    public String getEmailFromAccessToken(@NotNull String token) {
        log.info("Method getEmailFromAccessToken");
        return getEmailFromToken(token, securityProperties.getAccessSecret());
    }

    @Override
    public String getEmailFromRefreshToken(@NotNull String token) {
        log.trace("Method getEmailFromRefreshToken");
        return getEmailFromToken(token, securityProperties.getRefreshSecret());
    }

    private boolean validateToken(@NotNull String token,
                                  String secret) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            log.info("Return true");
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("Return false");
        return false;
    }

    private String getEmailFromToken(@NotNull String token,
                                     String secret) {
        log.info("Method getEmailFromToken");
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
