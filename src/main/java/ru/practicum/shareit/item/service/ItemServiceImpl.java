package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ListItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserMapper;

import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;
    private final ItemRequestDao itemRequestDao;

    @Override
    public ItemDto addItem(final long id, final ItemDto itemDto) {
        final UserDto owner = UserMapper.toUserDto(
            userDao.getUser(id).orElseThrow(() -> new EntityNotFoundException("User", id))
        );
        final Long itemRequestId = itemDto.getRequest();
        ItemRequest itemRequest = null;
        if (itemRequestId != null) {
            itemRequest = itemRequestDao.getItemRequest(itemRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Item request", id));
        }
        final Item newItem = itemDao.addItem(
            ItemMapper.toItem(itemDto.withOwner(owner), itemRequest)
        );
        log.info("New item created successfully.");

        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto getItem(final long id) {
        final Item item = itemDao.getItem(id).orElseThrow(() -> new EntityNotFoundException("Item", id));

        return ItemMapper.toItemDto(item);
    }

    @Override
    public Stream<ListItemDto> getItems(final long id) {
        return itemDao.getItems(id).map(ItemMapper::toListItemDto);
    }

    @Override
    public ItemDto updateItem(final long id, final long itemId, final PatchItemDto patchItemDto) {
        final ItemDto itemDto = getItem(itemId);
        if (itemDto.getOwner().getId() != id) {
            throw new ForbiddenException(id, itemId, "edit");
        }
        final ItemDto newItemDto = patchItemDto.patch(itemDto);
        final Long itemRequestId = newItemDto.getRequest();
        ItemRequest itemRequest = null;
        if (itemRequestId != null) {
            itemRequest = itemRequestDao.getItemRequest(itemRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Item request", id));
        }
        itemDao.updateItem(ItemMapper.toItem(newItemDto, itemRequest))
            .orElseThrow(() -> new EntityNotFoundException("Item", newItemDto.getId()));
        log.info("Item " + newItemDto.getId() + " updated successfully.");

        return newItemDto;
    }

    @Override
    public void deleteItem(long id, long itemId) {
        final ItemDto itemDto = getItem(itemId);
        if (itemDto.getOwner().getId() != id) {
            throw new ForbiddenException(id, itemId, "delete");
        }
        itemDao.deleteItem(id).orElseThrow(() -> new EntityNotFoundException("Item", id));
        log.info("Item " + id + " deleted successfully.");
    }

    @Override
    public Stream<ItemDto> searchItem(final String text) {
        return itemDao.searchItem(text).map(ItemMapper::toItemDto);
    }
}
