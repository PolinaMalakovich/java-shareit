package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Value
public class PatchUserDto {
    @Pattern(regexp = "\\S+")
    String name;
    @Email(message = "Email should be valid")
    String email;

    public UserDto patch(UserDto userDto) {
        return new UserDto(
                userDto.getId(),
                this.name == null ? userDto.getName() : this.name,
                this.email == null ? userDto.getEmail() : this.email
        );
    }
}
