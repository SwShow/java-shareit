package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UsergDto;

@Service
public class UsergateClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UsergateClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> getUsers() {
        return get("");
    }

    public ResponseEntity<Object> getUser(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> createUser(UsergDto requestDto) {
        return post("", requestDto);
    }

    public ResponseEntity<Object> updateUser(Long userId, UsergDto requestDto) {
        return patch("/" + userId, requestDto);
    }

    public void deleteUser(Long userId) {
        delete("/" + userId);
    }
}
