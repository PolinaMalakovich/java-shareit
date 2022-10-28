package ru.practicum.shareit.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class ShareItClient {
    private final RestTemplate rest;
    private final String apiBase;

    @Autowired
    public ShareItClient(
        @Value("${shareit.server.schema}") String schema,
        @Value("${shareit.server.host}") String host,
        @Value("${shareit.server.port}") int port,
        RestTemplateBuilder builder
    ) {
        this.rest = builder.build();
        this.apiBase = schema + "://" + host + ":" + port;
    }

    public ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    public ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        return get(path, null, parameters);
    }

    public ResponseEntity<Object> get(String path, long userId) {
        return get(path, userId, null);
    }

    public ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    public <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, null, body);
    }

    public <T> ResponseEntity<Object> post(String path, long userId, T body) {
        return post(path, userId, null, body);
    }

    public <T> ResponseEntity<Object> post(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    public <T> ResponseEntity<Object> put(String path, long userId, T body) {
        return put(path, userId, null, body);
    }

    public <T> ResponseEntity<Object> put(String path, long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body);
    }

    public <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, null, null, body);
    }

    public <T> ResponseEntity<Object> patch(String path, long userId) {
        return patch(path, userId, null, null);
    }

    public <T> ResponseEntity<Object> patch(String path, long userId, T body) {
        return patch(path, userId, null, body);
    }

    public <T> ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    public ResponseEntity<Object> delete(String path) {
        return delete(path, null, null);
    }

    public ResponseEntity<Object> delete(String path, long userId) {
        return delete(path, userId, null);
    }

    public ResponseEntity<Object> delete(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<Object> shareitServerResponse;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(getFullPath(path), method, requestEntity, Object.class, parameters);
            } else {
                shareitServerResponse = rest.exchange(getFullPath(path), method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(shareitServerResponse);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    private String getFullPath(String path) {
        return apiBase + path;
    }
}
