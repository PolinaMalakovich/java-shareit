package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingDtoBookerId;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Objects;

@Value
public class ItemDtoWithCommentsAndBookings {
    Long id;
    String name;
    String description;
    Boolean available;
    UserDto owner;
    Long request;
    List<CommentDto> comments;
    BookingDtoBookerId lastBooking;
    BookingDtoBookerId nextBooking;

    public boolean isAvailable() {
        return available;
    }

    public ItemDtoWithCommentsAndBookings withId(Long id) {
        return Objects.equals(this.id, id) ? this :
            new ItemDtoWithCommentsAndBookings(
                id,
                this.name,
                this.description,
                this.available,
                this.owner,
                this.request,
                this.comments,
                this.lastBooking,
                this.nextBooking
            );
    }

    public ItemDtoWithCommentsAndBookings withName(String name) {
        return Objects.equals(this.name, name) ? this :
            new ItemDtoWithCommentsAndBookings(
                this.id,
                name,
                this.description,
                this.available,
                this.owner,
                this.request,
                this.comments,
                this.lastBooking,
                this.nextBooking
            );
    }

    public ItemDtoWithCommentsAndBookings withDescription(String description) {
        return Objects.equals(this.description, description) ? this :
            new ItemDtoWithCommentsAndBookings(
                this.id,
                this.name,
                description,
                this.available,
                this.owner,
                this.request,
                this.comments,
                this.lastBooking,
                this.nextBooking
            );
    }

    public ItemDtoWithCommentsAndBookings withAvailable(Boolean available) {
        return Objects.equals(this.available, available) ? this :
            new ItemDtoWithCommentsAndBookings(
                this.id,
                this.name,
                this.description,
                available,
                this.owner,
                this.request,
                this.comments,
                this.lastBooking,
                this.nextBooking
            );
    }

    public ItemDtoWithCommentsAndBookings withOwner(UserDto owner) {
        return Objects.equals(this.owner, owner) ? this :
            new ItemDtoWithCommentsAndBookings(
                this.id,
                this.name,
                this.description,
                this.available,
                owner,
                this.request,
                this.comments,
                this.lastBooking,
                this.nextBooking
            );
    }

    public ItemDtoWithCommentsAndBookings withRequest(long request) {
        return Objects.equals(this.request, request) ? this :
            new ItemDtoWithCommentsAndBookings(
                this.id,
                this.name,
                this.description,
                this.available,
                this.owner,
                request,
                this.comments,
                this.lastBooking,
                this.nextBooking
            );
    }
}
