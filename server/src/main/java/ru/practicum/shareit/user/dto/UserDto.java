package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    @NotNull(message = "User name can't be null.")
    private String name;

    @NotNull(message = "User e-mail can't be null.")
    @Email(message = "User e-mail is incorrect.")
    private String email;

}
