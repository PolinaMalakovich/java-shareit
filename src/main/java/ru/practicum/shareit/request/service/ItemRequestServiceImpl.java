package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoForRequests;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.service.ItemMapper.toItemDtoForRequestsList;
import static ru.practicum.shareit.request.service.ItemRequestMapper.*;
import static ru.practicum.shareit.user.service.UserMapper.toUserDto;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto addRequest(long id, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        final ItemRequest itemRequest = toItemRequest(itemRequestDto.withRequester(toUserDto(user)));
        final ItemRequest newItemRequest = requestRepository.save(itemRequest);
        log.info("New request created successfully.");

        return toItemRequestDto(newItemRequest);
    }

    @Override
    public List<ItemRequestDtoWithAnswers> getRequests(long id) {
        userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        return requestRepository.findByRequesterIdOrderByCreatedDesc(id)
            .map(itemRequest -> toItemRequestDtoWithAnswers(itemRequest, getItemsByRequest(itemRequest.getId())))
            .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoWithAnswers> getAllRequests(long id, int from, int size) {
        return requestRepository
            .findByRequesterIdNot(id, PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")))
            .map(itemRequest -> toItemRequestDtoWithAnswers(itemRequest, getItemsByRequest(itemRequest.getId())))
            .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoWithAnswers getRequest(long id, long requestId) {
        userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        ItemRequest itemRequest = requestRepository.findById(requestId)
            .orElseThrow(() -> new EntityNotFoundException("request", requestId));
        List<Item> items = itemRepository.findByRequestIdOrderById(itemRequest.getId()).collect(Collectors.toList());

        return toItemRequestDtoWithAnswers(itemRequest, toItemDtoForRequestsList(items));
    }

    private List<ItemDtoForRequests> getItemsByRequest(long requestId) {
        return toItemDtoForRequestsList(
            itemRepository.findByRequestIdOrderById(requestId).collect(Collectors.toList())
        );
    }
}
