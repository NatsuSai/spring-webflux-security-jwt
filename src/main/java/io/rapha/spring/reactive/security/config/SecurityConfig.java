package io.rapha.spring.reactive.security.config;

import io.rapha.spring.reactive.security.auth.MyReactiveAuthenticationManager;
import io.rapha.spring.reactive.security.auth.MyReactiveAuthorizationManager;
import io.rapha.spring.reactive.security.auth.basic.BasicAuthenticationSuccessHandler;
import io.rapha.spring.reactive.security.auth.bearer.BearerTokenReactiveAuthenticationManager;
import io.rapha.spring.reactive.security.auth.bearer.ServerHttpBearerAuthenticationConverter;
import io.rapha.spring.reactive.security.service.MyReactiveUserDetailsService;
import io.rapha.spring.reactive.security.service.ResourceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

/**
 * @author liufuhong
 * @since 2019-05-24 14:48
 */

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

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
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveUserDetailsService userDetailsService, ResourceService resourceService) {

        http
                .authorizeExchange()
                .pathMatchers("/login", "/", "/api/authenticate")
                .permitAll()
                .and()
                .addFilterAt(basicAuthenticationFilter(userDetailsService), SecurityWebFiltersOrder.HTTP_BASIC)
                .authorizeExchange()
                    .pathMatchers("/api/**")
                    .access(myReactiveAuthorizationManager(resourceService))
                .and()
                .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    /**
     * Use the already implemented logic in  AuthenticationWebFilter and set a custom
     * SuccessHandler that will return a JWT when a user is authenticated with user/password
     * Create an AuthenticationManager using the UserDetailsService defined above
     *
     * @return AuthenticationWebFilter
     */
    private AuthenticationWebFilter basicAuthenticationFilter(ReactiveUserDetailsService userDetailsService){
        UserDetailsRepositoryReactiveAuthenticationManager authManager;
        AuthenticationWebFilter basicAuthenticationFilter;
        ServerAuthenticationSuccessHandler successHandler;

        authManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        successHandler = new BasicAuthenticationSuccessHandler();

        basicAuthenticationFilter = new AuthenticationWebFilter(authManager);
        basicAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);

        return basicAuthenticationFilter;

    }

    /**
     * Use the already implemented logic by AuthenticationWebFilter and set a custom
     * converter that will handle requests containing a Bearer token inside
     * the HTTP Authorization header.
     * Set a dummy authentication manager to this filter, it's not needed because
     * the converter handles this.
     *
     * @return bearerAuthenticationFilter that will authorize requests containing a JWT
     */
    private AuthenticationWebFilter bearerAuthenticationFilter(){
        AuthenticationWebFilter bearerAuthenticationFilter;
        ServerAuthenticationConverter bearerConverter;
        ReactiveAuthenticationManager authManager;

        authManager  = new BearerTokenReactiveAuthenticationManager();
        bearerAuthenticationFilter = new AuthenticationWebFilter(authManager);
        bearerConverter = new ServerHttpBearerAuthenticationConverter();

        bearerAuthenticationFilter.setServerAuthenticationConverter(bearerConverter);
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"));

        return bearerAuthenticationFilter;
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return new MyReactiveUserDetailsService();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService) {
        return new MyReactiveAuthenticationManager(userDetailsService);
    }

    /**
     * A custom UserDetailsService to provide quick user rights for Spring Security,
     * more formal implementations may be added as separated files and annotated as
     * a Spring stereotype.
     *
     * @return MapReactiveUserDetailsService an InMemory implementation of user details
     */
//    @Bean
//    public MapReactiveUserDetailsService userDetailsRepository() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("user")
//                .roles("USER", "ADMIN")
//                .build();
//        return new MapReactiveUserDetailsService(user);
//    }

    @Bean
    public MyReactiveAuthorizationManager myReactiveAuthorizationManager(ResourceService resourceService) {
        return new MyReactiveAuthorizationManager(resourceService);
    }
}
