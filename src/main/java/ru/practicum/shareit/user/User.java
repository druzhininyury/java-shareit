package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class User {

    private long id;

    private String name;

    @NotNull
    @Email(message = "Email is not valid.")
    @NotEmpty
    private String email;

}
