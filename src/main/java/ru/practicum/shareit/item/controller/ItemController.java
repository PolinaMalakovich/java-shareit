package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

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
    public ItemDtoWithCommentsAndBookings getItem(@RequestHeader("X-Sharer-User-Id") final long id,
                                                  @PathVariable final long itemId) {
        return itemService.getItemWithCommentsAndBookings(id, itemId);
    }

    @GetMapping
    public List<ListItemDto> getItems(@RequestHeader("X-Sharer-User-Id") final long id) {
        return itemService.getItems(id);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") final long id, @PathVariable final long itemId) {
        itemService.deleteItem(id, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam final String text) {
        return itemService.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") final long id,
                                 @PathVariable final long itemId,
                                 @Valid @RequestBody final CommentDto commentDto) {
        return itemService.addComment(id, itemId, commentDto);
    }
}


