package io.natsusai.spring.reactive.security.auth;

import io.natsusai.spring.reactive.security.config.AuthoritiesConstants;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author liufuhong
 * @since 2019-05-27 16:21
 */

@Service
public class ResourceService {
    public Set<String> findByURIAndMethod(String path, HttpMethod method) {
        HashSet<String> set = new HashSet<>();
        set.add(AuthoritiesConstants.USER);
        return set;
    }
}
