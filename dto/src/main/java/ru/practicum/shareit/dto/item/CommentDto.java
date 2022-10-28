package ru.practicum.shareit.dto.item;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Value
public class CommentDto {
    long id;
    @NotBlank
    String text;
    long itemId;
    long authorId;
    String authorName;
    LocalDateTime created;
}
