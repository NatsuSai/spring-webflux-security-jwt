package io.natsusai.spring.reactive.security.config;

import io.natsusai.spring.reactive.security.auth.CustomAuthorityReactiveAuthorizationManager;
import io.natsusai.spring.reactive.security.auth.DomainReactiveUserDetailsService;
import io.natsusai.spring.reactive.security.auth.jwt.JWTReactiveAuthenticationManager;
import io.natsusai.spring.reactive.security.auth.jwt.ServerJWTAuthenticationConverter;
import io.natsusai.spring.reactive.security.auth.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

/**
 * @author liufuhong
 * @since 2019-05-27 11:18
 */

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {
    /**
     * For Spring Security webflux, a chain of filters will provide user authentication
     * and authorization, we add custom filters to enable JWT token approach.
     *
     * @param http An initial object to build common filter scenarios.
     *             Customized filters are added here.
     * @return SecurityWebFilterChain A filter chain for web exchanges that will
     * provide security
     **/
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, TokenProvider tokenProvider) {

        http
                .formLogin().and()
                .authorizeExchange()
                .pathMatchers("/login", "/", "/jwt/**", "/dubbo")
                .permitAll()
                .and()
                .addFilterAt(jwtAuthenticationWebFilter(tokenProvider), SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange()
                .pathMatchers("/**")
                .access(customAuthorityReactiveAuthorizationManager());
        return http.build();
    }

    /**
     * Use the already implemented logic by AuthenticationWebFilter and set a custom
     * converter that will handle requests containing a Bearer token inside
     * the HTTP Authorization header.
     * Set a dummy authentication manager to this filter, it's not needed because
     * the converter handles this.
     *
     * @return jwtAuthenticationWebFilter that will authorize requests containing a JWT
     */
    private AuthenticationWebFilter jwtAuthenticationWebFilter(TokenProvider tokenProvider) {
        AuthenticationWebFilter jwtAuthenticationWebFilter;
        ServerAuthenticationConverter jwtConverter;
        ReactiveAuthenticationManager authManager;

        authManager  = new JWTReactiveAuthenticationManager();
        jwtAuthenticationWebFilter = new AuthenticationWebFilter(authManager);
//        jwtAuthenticationWebFilter = new JWTAuthenticationWebFilter(authManager);
        jwtConverter = new ServerJWTAuthenticationConverter(tokenProvider);

        jwtAuthenticationWebFilter.setServerAuthenticationConverter(jwtConverter);
        jwtAuthenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));

        return jwtAuthenticationWebFilter;
    }

    @Bean
    public ReactiveUserDetailsService domainReactiveUserDetailsService() {
        return new DomainReactiveUserDetailsService();
    }

    @Bean
    public ReactiveAuthorizationManager<AuthorizationContext> customAuthorityReactiveAuthorizationManager() {
        return new CustomAuthorityReactiveAuthorizationManager();
    }
}
