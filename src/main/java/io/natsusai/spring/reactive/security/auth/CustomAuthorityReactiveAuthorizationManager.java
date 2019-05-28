package io.natsusai.spring.reactive.security.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * @author liufuhong
 * @since 2019-05-27 16:17
 */

@Slf4j
public class CustomAuthorityReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Autowired
    private ResourceService resourceService;

    private static final String ADMIN = "ROLE_ADMIN";

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext context) {
        ServerHttpRequest request = context.getExchange().getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();
        log.info("{} - {} : [{}][{}]", this.getClass().getSimpleName(), "check", method.name(), path);
        Set<String> permissions = resourceService.findByURIAndMethod(path, method);

        if (CollectionUtils.isEmpty(permissions)) return Mono.just(new AuthorizationDecision(true));
        permissions.add(ADMIN);
        return mono
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .map(permissions::contains)
                .hasElement(true)
                .map(AuthorizationDecision::new).log();
    }
}
