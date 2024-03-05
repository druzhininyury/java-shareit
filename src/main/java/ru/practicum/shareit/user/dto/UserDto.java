package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {

    private Long id;

    @NotNull(message = "User name can't be null.")
    private String name;

    @NotNull(message = "User e-mail can't be null.")
    @Email(message = "User e-mail is incorrect.")
    private String email;

}
