package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @InjectMocks
    UserController userController;
    @Mock
    private UserService userService;
    private final UserDto userDto = new UserDto(0L, "test","test@mail.com");
    private final UserDto userDto1 = new UserDto(1L, "test","test@mail.com");
    private final UserDto userDto2 = new UserDto(1L, "update", "update@mail.ru");

    @Test
    public void createUser() {

        when(userService.createUser(any()))
                .thenReturn(userDto1);

        ResponseEntity<UserDto>  res = userController.create(userDto);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(res.getBody()).getId()).isEqualTo(1);

    }

   @Test
    public void updateUser() {
       when(userService.updateUser(userDto1, 1L))
               .thenReturn(userDto2);

       ResponseEntity<UserDto>  res = userController.updateUser(1L, userDto1);
       assertThat(res.getStatusCodeValue()).isEqualTo(200);
       assertThat(Objects.requireNonNull(res.getBody()).getId()).isEqualTo(1);
       assertThat(Objects.requireNonNull(res.getBody()).getName()).isEqualTo(userDto2.getName());
    }

    @Test
    public void findAllUsers() {
        when(userService.findAllUsers())
                .thenReturn(List.of(userDto1));

        ResponseEntity<List<UserDto>> res = userController.findAllUsers();
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(res.getBody()).size()).isEqualTo(1);
    }

    @Test
    public void findUserById() {
        when(userService.findUserById(anyLong()))
                .thenReturn(userDto2);

        ResponseEntity<UserDto>  res = userController.findUserById(1L);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(res.getBody()).getName()).isEqualTo(userDto2.getName());
    }

    @Test
    public void deleteUser() {
        ResponseEntity<?> res = userController.deleteUser(1L);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
    }

}