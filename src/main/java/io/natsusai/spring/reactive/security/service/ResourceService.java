package io.natsusai.spring.reactive.security.service;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author Kurenai
 * @since 2019-05-26 17:54
 */

@Service
public class ResourceService {
    public List<String> getPermission(String path, HttpMethod method) {
        return Arrays.asList("ROLE_USER","ROLE_ADMIN");
    }
}
