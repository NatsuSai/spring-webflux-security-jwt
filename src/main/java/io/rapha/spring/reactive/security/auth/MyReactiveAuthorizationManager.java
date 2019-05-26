package io.rapha.spring.reactive.security.auth;

import com.sun.org.apache.regexp.internal.RE;
import io.rapha.spring.reactive.security.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * @author Kurenai
 * @since 2019-05-26 16:47
 */

public class MyReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private ResourceService resourceService;

    public MyReactiveAuthorizationManager(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        ServerHttpRequest request = context.getExchange().getRequest();
        HttpMethod method = request.getMethod();
        String path = request.getURI().getPath();
        List<String> permissions = resourceService.getPermission(path, method);
        Set<String> permissionSet = new HashSet<>(permissions);

        return authentication
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .filter(permissionSet::contains)
                .hasElements()
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false)).log();
    }

}
