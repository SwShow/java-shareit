package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class UsergDto {

    Long id;
    @NotBlank
    String name;
    @NotBlank
    @Email
    String email;
}
