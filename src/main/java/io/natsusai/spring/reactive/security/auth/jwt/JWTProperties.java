package io.natsusai.spring.reactive.security.auth.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author liufuhong
 * @since 2019-05-27 11:56
 */

@ConfigurationProperties(
        prefix = "jwt",
        ignoreUnknownFields = false
)
@Data
@Configuration
public class JWTProperties {

    private String secret;
    private String base64Secret;
    private long tokenValidityInSeconds = 1800L;
    private long tokenValidityInSecondsForRememberMe = 2592000L;

}
