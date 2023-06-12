package eu.senla.userservice.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Period;

@Configuration
@ConfigurationProperties(prefix = "token")
@Setter
@Getter
@Valid
public class JwtSecurityProperties {

    @NotNull
    private String accessSecret;
    @NotNull
    private String refreshSecret;
    @NotNull
    private Period accessTime;
    @NotNull
    private Period refreshTime;
}
