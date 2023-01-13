package ru.practicum.shareit.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    private final UserDto userDto = new UserDto(0L, "test", "test@mail.com");
    private final UserDto userDto1 = new UserDto(1L, "test", "test@mail.com");
    private final UserDto userDto2 = new UserDto(1L, "update", "update@mail.ru");

    @Test
    public void createUser() {

        when(userService.createUser(any()))
                .thenReturn(userDto1);

        UserDto res = userController.create(userDto);
        assertEquals(res.getId(), 1);

    }

    @Test
    public void updateUser() {
        when(userService.updateUser(userDto1, 1L))
                .thenReturn(userDto2);

        UserDto res = userController.updateUser(1L, userDto1);
        assertEquals(res.getId(), 1);
        assertEquals(res.getName(), userDto2.getName());
    }

    @Test
    public void findAllUsers() {
        when(userService.findAllUsers())
                .thenReturn(List.of(userDto1));

        List<UserDto> res = userController.findAllUsers();
        assertEquals(res.size(), 1);
    }

    @Test
    public void findUserById() {
        when(userService.findUserById(anyLong()))
                .thenReturn(userDto2);

        UserDto res = userController.findUserById(1L);
        assertEquals(res.getName(), userDto2.getName());
    }

}
