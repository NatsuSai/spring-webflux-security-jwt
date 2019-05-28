package io.natsusai.spring.reactive.security.api;

import io.jsonwebtoken.Jwt;
import io.natsusai.spring.reactive.security.auth.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author liufuhong
 * @since 2019-05-28 9:22
 */

@RestController
@RequestMapping("/jwt")
public class JWTController {

    @Autowired
    private ReactiveUserDetailsService reactiveUserDetailsService;

    private final TokenProvider tokenProvider;

    public JWTController(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/authenticate")
    public Flux<String> authenticate(@RequestParam String username, @RequestParam String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);
        return authenticationManager.authenticate(authenticationToken)
                .map(tokenProvider::createToken)
                .map(token -> String.join(" ", "Bearer", token))
                .flux();
    }

    @GetMapping("/createToken")
    public Flux<String> createToken(@RequestParam String username, @RequestParam String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return Mono.justOrEmpty(tokenProvider.createToken(authenticationToken)).flux();
    }

    @GetMapping("/parseToken")
    public Flux<Jwt> parseToken(@RequestParam String token) {
        return Mono.justOrEmpty(tokenProvider.validateToken(token)).switchIfEmpty(Mono.empty()).flux();
    }
}
