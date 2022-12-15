package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
