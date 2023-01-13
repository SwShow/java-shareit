package ru.practicum.shareit.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.shareit.user.model.User;

import java.util.List;


@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @BeforeEach
    public void prepare() {
        User user1 = new User();
        user1.setName("test");
        user1.setEmail("test@mail.ru");

        User user2 = new User();
        user2.setName("test2");
        user2.setEmail("test2@mail.ru");
    }

    @Test
    public void findAllWithEmptyRepository_shouldReturnEmpty() {
        List<User> users = repository.findAll();

        Assertions.assertEquals(0, users.size());
    }
}