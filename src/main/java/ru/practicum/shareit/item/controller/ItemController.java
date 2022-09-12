package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ListItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.stream.Stream;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") final long id,
                           @Valid @RequestBody final ItemDto itemDto) {
        return itemService.addItem(id, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") final long id,
                              @PathVariable final long itemId,
                              @RequestBody final PatchItemDto patchItemDto) {
        return itemService.updateItem(id, itemId, patchItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable final long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public Stream<ListItemDto> getItems(@RequestHeader("X-Sharer-User-Id") final long id) {
        return itemService.getItems(id);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") final long id, @PathVariable final long itemId) {
        itemService.deleteItem(id, itemId);
    }

    @GetMapping("/search")
    public Stream<ItemDto> searchItem(final String text) {
        return itemService.searchItem(text);
    }
}


