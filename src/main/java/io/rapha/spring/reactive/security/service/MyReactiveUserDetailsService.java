package io.rapha.spring.reactive.security.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

/**
 * @author liufuhong
 * @since 2019-05-24 14:46
 */

public class MyReactiveUserDetailsService implements ReactiveUserDetailsService {

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.justOrEmpty(User.withDefaultPasswordEncoder()
                                    .username("user")
                                    .password("user")
                                    .roles("USER")
                                    .build());
    }
}
