package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoBookerId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.UnauthorizedCommentException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private final long randomId = 42;
    private final User john = new User(1L, "John", "john.doe@example.com");
    private final UserDto johnDto = new UserDto(john.getId(), john.getName(), john.getEmail());
    private final User alice = new User(2L, "Alice", "alice.anderson@example.com");
    private final UserDto aliceDto = new UserDto(alice.getId(), alice.getName(), alice.getEmail());
    private final ItemRequest coffeeTableRequest = new ItemRequest(
        4L,
        "Vintage coffee table, preferably wooden",
        john,
        LocalDateTime.of(2022, 10, 11, 15, 14)
    );
    private final Item coffeeTable = new Item(
        3L,
        "Coffee table",
        "Old wooden coffee table",
        true,
        alice,
        coffeeTableRequest
    );
    private final ItemDto coffeeTableDto = new ItemDto(
        coffeeTable.getId(),
        coffeeTable.getName(),
        coffeeTable.getDescription(),
        coffeeTable.isAvailable(),
        aliceDto,
        coffeeTable.getRequest().getId() == null ? null : coffeeTable.getRequest().getId()
    );
    private final Booking lastBooking = new Booking(
        6L,
        LocalDateTime.of(2022, 10, 11, 16, 28),
        LocalDateTime.of(2022, 10, 13, 23, 0),
        coffeeTable,
        john,
        Status.APPROVED
    );
    private final Booking nextBooking = new Booking(
        7L,
        LocalDateTime.of(2022, 10, 14, 17, 35),
        LocalDateTime.of(2022, 10, 15, 23, 0),
        coffeeTable,
        john,
        Status.APPROVED
    );
    private final Comment niceComment = new Comment(
        8L,
        "Nice!",
        coffeeTable,
        john,
        LocalDateTime.of(2022, 10, 12, 15, 53)
    );
    private final CommentDto niceCommentDto = new CommentDto(
        niceComment.getId(),
        niceComment.getText(),
        niceComment.getItem().getId(),
        niceComment.getAuthor().getId(),
        niceComment.getAuthor().getName(),
        niceComment.getCreated()
    );
    @Mock
    ItemRepository mockItemRepository;
    @Mock
    UserRepository mockUserRepository;
    @Mock
    RequestRepository mockRequestRepository;
    @Mock
    BookingRepository mockBookingRepository;
    @Mock
    CommentRepository mockCommentRepository;
    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void addItemWhenUserDoesNotExist() {
        Mockito.when(mockUserRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.addItem(randomId, coffeeTableDto));
    }

    @Test
    void addItemWhenRequestDoesNotExist() {
        Mockito.when(mockUserRepository.findById(alice.getId())).thenReturn(Optional.of(alice));
        Mockito.when(mockRequestRepository.findById(coffeeTableDto.getRequestId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.addItem(alice.getId(), coffeeTableDto));
    }

    @Test
    void addItem() {
        Mockito.when(mockUserRepository.findById(alice.getId())).thenReturn(Optional.of(alice));
        Mockito
            .when(mockRequestRepository.findById(coffeeTableDto.getRequestId()))
            .thenReturn(Optional.of(coffeeTableRequest));
        Mockito
            .when(mockItemRepository.save(any(Item.class)))
            .thenReturn(coffeeTable);
        ItemDto actual = itemService.addItem(alice.getId(), coffeeTableDto);

        assertEquals(coffeeTableDto, actual);
    }

    @Test
    void getItemWhenItemDoesNotExist() {
        Mockito.when(mockItemRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItem(randomId));
    }

    @Test
    void getItem() {
        Mockito.when(mockItemRepository.findById(coffeeTable.getId())).thenReturn(Optional.of(coffeeTable));
        ItemDto actual = itemService.getItem(coffeeTable.getId());

        assertEquals(coffeeTableDto, actual);
    }

    @Test
    void getItemWithCommentsAndBookingsWhenItemDoesNotExist() {
        Mockito.when(mockItemRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> itemService.getItemWithCommentsAndBookings(alice.getId(), randomId)
        );
    }

    @Test
    void getItemWithCommentsAndBookingsWhenUserIsOwner() {
        ItemDtoWithCommentsAndBookings expected = new ItemDtoWithCommentsAndBookings(
            coffeeTable.getId(),
            coffeeTable.getName(),
            coffeeTable.getDescription(),
            coffeeTable.isAvailable(),
            aliceDto,
            coffeeTable.getRequest().getId(),
            new ArrayList<>(),
            new BookingDtoBookerId(
                6L,
                LocalDateTime.of(2022, 10, 11, 16, 28),
                LocalDateTime.of(2022, 10, 13, 23, 0),
                coffeeTableDto,
                john.getId(),
                Status.APPROVED
            ),
            new BookingDtoBookerId(
                7L,
                LocalDateTime.of(2022, 10, 14, 17, 35),
                LocalDateTime.of(2022, 10, 15, 23, 0),
                coffeeTableDto,
                john.getId(),
                Status.APPROVED
            )
        );
        Mockito.when(mockItemRepository.findById(coffeeTable.getId())).thenReturn(Optional.of(coffeeTable));
        Mockito
            .when(mockBookingRepository.getPastOrCurrentBookingByItemId(coffeeTable.getId()))
            .thenReturn(Optional.of(lastBooking));
        Mockito
            .when(mockBookingRepository.getFutureBookingByItemId(coffeeTable.getId()))
            .thenReturn(Optional.of(nextBooking));
        Mockito
            .when(mockCommentRepository.findCommentsByItem_Id(coffeeTable.getId()))
            .thenReturn(Stream.empty());

        ItemDtoWithCommentsAndBookings actual = itemService.getItemWithCommentsAndBookings(
            alice.getId(),
            coffeeTable.getId()
        );

        assertEquals(expected, actual);
    }

    @Test
    void getItemWithCommentsAndBookingsWhenUserIsNotOwner() {
        ItemDtoWithCommentsAndBookings expected = new ItemDtoWithCommentsAndBookings(
            coffeeTable.getId(),
            coffeeTable.getName(),
            coffeeTable.getDescription(),
            coffeeTable.isAvailable(),
            aliceDto,
            coffeeTable.getRequest().getId(),
            new ArrayList<>(),
            null,
            null
        );
        Mockito.when(mockItemRepository.findById(coffeeTable.getId())).thenReturn(Optional.of(coffeeTable));
        Mockito
            .when(mockCommentRepository.findCommentsByItem_Id(coffeeTable.getId()))
            .thenReturn(Stream.empty());
        ItemDtoWithCommentsAndBookings actual = itemService.getItemWithCommentsAndBookings(
            john.getId(),
            coffeeTable.getId()
        );

        assertEquals(expected, actual);
    }

    @Test
    void getItems() {
        Mockito
            .when(mockItemRepository.findByOwnerIdOrderById(alice.getId(), PageRequest.of(0, 1)))
            .thenReturn(Stream.of(coffeeTable));
        Mockito
            .when(mockCommentRepository.findCommentsByItem_Id(coffeeTable.getId()))
            .thenReturn(Stream.empty());
        Mockito
            .when(mockBookingRepository.getPastOrCurrentBookingByItemId(coffeeTable.getId()))
            .thenReturn(Optional.of(lastBooking));
        Mockito
            .when(mockBookingRepository.getFutureBookingByItemId(coffeeTable.getId()))
            .thenReturn(Optional.of(nextBooking));
        ListItemDto expected = new ListItemDto(
            coffeeTable.getId(),
            coffeeTable.getName(),
            coffeeTable.getDescription(),
            coffeeTable.isAvailable(),
            new ArrayList<>(),
            new BookingDtoBookerId(
                6L,
                LocalDateTime.of(2022, 10, 11, 16, 28),
                LocalDateTime.of(2022, 10, 13, 23, 0),
                coffeeTableDto,
                john.getId(),
                Status.APPROVED
            ),
            new BookingDtoBookerId(
                7L,
                LocalDateTime.of(2022, 10, 14, 17, 35),
                LocalDateTime.of(2022, 10, 15, 23, 0),
                coffeeTableDto,
                john.getId(),
                Status.APPROVED
            )
        );
        List<ListItemDto> actual = itemService.getItems(alice.getId(), 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void updateItemWhenItemDoesNotExist() {
        Mockito
            .when(mockItemRepository.findById(randomId))
            .thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> itemService.updateItem(
                alice.getId(),
                randomId,
                new PatchItemDto(null, null, null)
            )
        );
    }

    @Test
    void updateItemWhenUserIsNotOwner() {
        Mockito
            .when(mockItemRepository.findById(coffeeTable.getId()))
            .thenReturn(Optional.of(coffeeTable));

        assertThrows(
            ForbiddenException.class,
            () -> itemService.updateItem(
                john.getId(),
                coffeeTable.getId(),
                new PatchItemDto(null, null, null)
            )
        );
    }

    @Test
    void updateItemWithNulls() {
        Item tuxedo = new Item(5L, "Tuxedo", "Notch lapel modern fit tuxedo, black.", true, john, null);
        PatchItemDto update = new PatchItemDto(null, null, null);
        ItemDto expected = new ItemDto(
            tuxedo.getId(),
            tuxedo.getName(),
            tuxedo.getDescription(),
            tuxedo.isAvailable(),
            johnDto,
            null
        );
        Mockito
            .when(mockItemRepository.findById(tuxedo.getId()))
            .thenReturn(Optional.of(tuxedo));
        ItemDto actual = itemService.updateItem(john.getId(), tuxedo.getId(), update);

        assertEquals(expected, actual);
    }

    @Test
    void updateItem() {
        Item tuxedo = new Item(5L, "Tuxedo", "Notch lapel modern fit tuxedo, black.", true, john, null);
        PatchItemDto update = new PatchItemDto("Black tuxedo", "Notch lapel modern fit tuxedo.", false);
        ItemDto expected = new ItemDto(
            tuxedo.getId(),
            update.getName(),
            update.getDescription(),
            update.getAvailable(),
            johnDto,
            null
        );
        Mockito
            .when(mockItemRepository.findById(tuxedo.getId()))
            .thenReturn(Optional.of(tuxedo));
        ItemDto actual = itemService.updateItem(john.getId(), tuxedo.getId(), update);

        assertEquals(expected, actual);
    }

    @Test
    void deleteItemWhenItemDoesNotExist() {
        Mockito.when(mockItemRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.deleteItem(alice.getId(), randomId));
    }

    @Test
    void deleteItemWhenUserIsNotOwner() {
        Mockito.when(mockItemRepository.findById(coffeeTable.getId())).thenReturn(Optional.of(coffeeTable));

        assertThrows(ForbiddenException.class, () -> itemService.deleteItem(john.getId(), coffeeTable.getId()));
    }

    @Test
    void deleteItem() {
        Item tuxedo = new Item(5L, "Tuxedo", "Notch lapel modern fit tuxedo, black.", true, john, null);
        Mockito.when(mockItemRepository.findById(tuxedo.getId())).thenReturn(Optional.of(tuxedo));
        itemService.deleteItem(john.getId(), tuxedo.getId());

        Mockito.verify(mockItemRepository, Mockito.times(1)).deleteById(tuxedo.getId());
    }

    @Test
    void searchItemWhenTextIsBlank() {
        List<ItemDto> actual = itemService.searchItem("", 0, 1);

        assertEquals(Collections.emptyList(), actual);
    }

    @Test
    void searchItem() {
        Mockito
            .when(mockItemRepository.searchItem("table", PageRequest.of(0, 1)))
            .thenReturn(Stream.of(coffeeTable));
        List<ItemDto> actual = itemService.searchItem("table", 0, 1);

        assertEquals(List.of(coffeeTableDto), actual);
    }

    @Test
    void addCommentWhenItemDoesNotExist() {
        Mockito
            .when(mockItemRepository.findById(randomId))
            .thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> itemService.addComment(
                john.getId(),
                randomId,
                niceCommentDto
            )
        );
    }

    @Test
    void addCommentWhenUserDoesNotExist() {
        Mockito
            .when(mockItemRepository.findById(coffeeTable.getId()))
            .thenReturn(Optional.of(coffeeTable));
        Mockito
            .when(mockUserRepository.findById(randomId))
            .thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> itemService.addComment(
                randomId,
                coffeeTable.getId(),
                niceCommentDto
            )
        );
    }

    @Test
    void addCommentWhenUserIsNotBooker() {
        Mockito
            .when(mockItemRepository.findById(coffeeTable.getId()))
            .thenReturn(Optional.of(coffeeTable));
        Mockito
            .when(mockUserRepository.findById(john.getId()))
            .thenReturn(Optional.of(john));
        Mockito
            .when(mockBookingRepository.findBookingsByItem_IdAndBooker_IdAndEndIsBefore(
                    eq(coffeeTable.getId()),
                    eq(john.getId()),
                    any(LocalDateTime.class)
                )
            )
            .thenReturn(Stream.empty());

        assertThrows(
            UnauthorizedCommentException.class,
            () -> itemService.addComment(john.getId(), coffeeTable.getId(), niceCommentDto)
        );
    }

    @Test
    void addComment() {
        Mockito
            .when(mockItemRepository.findById(coffeeTable.getId()))
            .thenReturn(Optional.of(coffeeTable));
        Mockito
            .when(mockUserRepository.findById(john.getId()))
            .thenReturn(Optional.of(john));
        Mockito
            .when(mockBookingRepository.findBookingsByItem_IdAndBooker_IdAndEndIsBefore(
                    eq(coffeeTable.getId()),
                    eq(john.getId()),
                    any(LocalDateTime.class)
                )
            )
            .thenReturn(Stream.of(lastBooking));
        Mockito.when(mockCommentRepository.save(any())).thenReturn(niceComment);
        CommentDto actual = itemService.addComment(john.getId(), coffeeTable.getId(), niceCommentDto);

        assertEquals(niceCommentDto, actual);
    }
}