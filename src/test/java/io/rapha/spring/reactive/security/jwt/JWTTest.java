package io.rapha.spring.reactive.security.jwt;

import com.nimbusds.jwt.SignedJWT;
import io.rapha.spring.reactive.security.auth.bearer.ServerHttpBearerAuthenticationConverter;
import io.rapha.spring.reactive.security.auth.jwt.AuthorizationHeaderPayload;
import io.rapha.spring.reactive.security.auth.jwt.JWTCustomVerifier;
import io.rapha.spring.reactive.security.auth.jwt.UsernamePasswordAuthenticationBearer;
import jdk.nashorn.internal.parser.Token;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author liufuhong
 * @since 2019-05-24 12:40
 */

public class JWTTest {

    private String toke = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImlzcyI6InJhcGhhLmlvIiwiZXhwIjoxNTU4NzU2OTAzfQ.aegWDqvcT8WXvepPNAYgzIpXqNLdVZaPGbtTCsIpL1Y";
    private static final String BEARER = "Bearer ";
    private static final Predicate<String> matchBearerLength = authValue -> authValue.length() > BEARER.length();
    private static final Function<String,Mono<String>> isolateBearerValue = authValue -> Mono.justOrEmpty(authValue.substring(BEARER.length()));

    @Test
    public void TestVerifier() throws ParseException {
        SignedJWT parse = null;
        try {
            parse = SignedJWT.parse(toke);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(parse.getJWTClaimsSet().getClaims());
    }

    @Test
    public void testConverter() {
        JWTCustomVerifier verifier = new JWTCustomVerifier();
        ServerHttpBearerAuthenticationConverter converter = new ServerHttpBearerAuthenticationConverter();
        Mono<Authentication> mono = Mono.justOrEmpty(toke)
                .filter(matchBearerLength)
                .flatMap(isolateBearerValue)
                .flatMap(verifier::check)
                .flatMap(UsernamePasswordAuthenticationBearer::create).log();
        System.out.println(mono);
    }
}
