package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoForRequests;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    private final long randomId = 42;
    private final User john = new User(1L, "John", "john.doe@example.com");
    private final UserDto johnDto = new UserDto(john.getId(), john.getName(), john.getEmail());
    private final User alice = new User(2L, "Alice", "alice.anderson@example.com");
    private final UserDto aliceDto = new UserDto(alice.getId(), alice.getName(), alice.getEmail());
    private final ItemRequest tableRequest = new ItemRequest(
        4L,
        "Vintage coffee table, preferably wooden",
        john,
        LocalDateTime.of(2022, 10, 11, 15, 14)
    );
    private final ItemRequestDto tableRequestDto = new ItemRequestDto(
        tableRequest.getId(),
        tableRequest.getDescription(),
        johnDto,
        tableRequest.getCreated()
    );
    private final Item coffeeTable = new Item(
        3L,
        "Coffee table",
        "Old wooden coffee table",
        true,
        alice,
        tableRequest
    );
    private final ItemDtoForRequests tableForRequests = new ItemDtoForRequests(
        coffeeTable.getId(),
        coffeeTable.getName(),
        coffeeTable.getDescription(),
        coffeeTable.isAvailable(),
        coffeeTable.getRequest().getId()
    );
    private final ItemRequestDtoWithAnswers tableRequestWithAnswers = new ItemRequestDtoWithAnswers(
        tableRequest.getId(),
        tableRequest.getDescription(),
        tableRequest.getCreated(),
        List.of(tableForRequests)
    );
    @Mock
    RequestRepository mockRequestRepository;
    @Mock
    ItemRepository mockItemRepository;
    @Mock
    UserRepository mockUserRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Test
    void addRequestWhenUserDoesNotExist() {
        Mockito.when(mockUserRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.addRequest(randomId, tableRequestDto));
    }

    @Test
    void addRequest() {
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito.when(mockRequestRepository.save(any())).thenReturn(tableRequest);
        ItemRequestDto actual = itemRequestService.addRequest(john.getId(), tableRequestDto);

        assertEquals(tableRequestDto, actual);
    }

    @Test
    void getRequestsWhenUserDoesNotExist() {
        Mockito.when(mockUserRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getRequests(randomId));
    }

    @Test
    void getRequests() {
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(mockRequestRepository.findByRequesterIdOrderByCreatedDesc(john.getId()))
            .thenReturn(Stream.of(tableRequest));
        Mockito
            .when(mockItemRepository.findByRequestIdOrderById(tableRequest.getId()))
            .thenReturn(Stream.of(coffeeTable));
        List<ItemRequestDtoWithAnswers> actual = itemRequestService.getRequests(john.getId());

        assertEquals(List.of(tableRequestWithAnswers), actual);
    }

    @Test
    void getAllRequests() {
        Mockito
            .when(mockRequestRepository.findByRequesterIdNot(
                    alice.getId(),
                    PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "created"))
                )
            )
            .thenReturn(Stream.of(tableRequest));
        Mockito
            .when(mockItemRepository.findByRequestIdOrderById(tableRequest.getId()))
            .thenReturn(Stream.of(coffeeTable));
        List<ItemRequestDtoWithAnswers> actual = itemRequestService.getAllRequests(alice.getId(), 0, 1);

        assertEquals(List.of(tableRequestWithAnswers), actual);
    }

    @Test
    void getRequestWhenUserDoesNotExist() {
        Mockito.when(mockUserRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> itemRequestService.getRequest(randomId, tableRequest.getId())
        );
    }

    @Test
    void getRequestWhenRequestDoesNotExist() {
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(mockRequestRepository.findById(randomId))
            .thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> itemRequestService.getRequest(john.getId(), randomId)
        );
    }

    @Test
    void getRequest() {
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(mockRequestRepository.findById(tableRequest.getId()))
            .thenReturn(Optional.of(tableRequest));
        Mockito
            .when(mockItemRepository.findByRequestIdOrderById(tableRequest.getId()))
            .thenReturn(Stream.of(coffeeTable));
        ItemRequestDtoWithAnswers actual = itemRequestService.getRequest(john.getId(), tableRequest.getId());

        assertEquals(tableRequestWithAnswers, actual);
    }
}