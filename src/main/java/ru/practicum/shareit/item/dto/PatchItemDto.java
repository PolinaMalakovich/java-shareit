package ru.practicum.shareit.item.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
public class PatchItemDto {
    @NotBlank(message = "Name cannot be blank")
    String name;
    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    String description;
    Boolean available;

    public Boolean isAvailable() {
        return available;
    }

    public ItemDto patch(ItemDto itemDto) {
        return new ItemDto(
                itemDto.getId(),
                this.name == null ? itemDto.getName() : this.name,
                this.description == null ? itemDto.getDescription() : this.description,
                this.available == null ? itemDto.isAvailable() : this.available,
                itemDto.getOwner(),
                itemDto.getRequest()
        );
    }
}
