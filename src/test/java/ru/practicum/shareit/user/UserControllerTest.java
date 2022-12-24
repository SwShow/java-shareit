package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    private UserController userController;

    UserDto user = new UserDto(0L, "User_name", "username@yandex.ru");

    @Test
    void createAndFindUserById() {

        ResponseEntity<UserDto> entity = userController.create(user);
        UserDto dto = entity.getBody();
        assertEquals(OK, entity.getStatusCode());
        assert dto != null;
        ResponseEntity<UserDto> entity1 = userController.findUserById(dto.getId());
        UserDto dto1 =  entity1.getBody();
        assertEquals(OK, entity1.getStatusCode());
        assert dto1 != null;
        assertEquals(dto.getId(), dto1.getId());

    }

    @Test
    void updateUser() {
        ResponseEntity<UserDto> entity1 = userController.create(user);
        UserDto dto = entity1.getBody();
        assert dto != null;
        dto.setName("Update_name");
        dto.setEmail("updatename@mail.com");
        ResponseEntity<UserDto> entity = userController.updateUser(dto.getId(), dto);
        UserDto dto1 = entity.getBody();
        assertEquals(OK, entity.getStatusCode());
        assert dto1 != null;
        assertEquals(1L, dto1.getId());
        assertEquals("Update_name", dto1.getName());
        assertEquals("updatename@mail.com", dto1.getEmail());
    }

    @Test
    void deleteUser() {
        ResponseEntity<UserDto> entity = userController.create(user);
        UserDto dto = entity.getBody();
        ResponseEntity<List<UserDto>> list = userController.findAllUsers();
        assertEquals(OK, list.getStatusCode());
        assertEquals(1, Objects.requireNonNull(list.getBody()).size());
        assert dto != null;
        ResponseEntity<?> del = userController.deleteUser(dto.getId());
        assertEquals(OK, del.getStatusCode());
        ResponseEntity<List<UserDto>> list1 = userController.findAllUsers();
        assertEquals(0, Objects.requireNonNull(list1.getBody()).size());
    }

    @Test
    void getByWrongId() {
        assertThrows(NoSuchElementException.class, () -> userController.findUserById(10L));
    }

    @Test
    void updateByWrongId() {
        assertThrows(NoSuchElementException.class, () -> userController.updateUser(10L, user));
    }
}