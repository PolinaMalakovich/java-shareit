package ru.practicum.shareit.server.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.item.*;
import ru.practicum.shareit.server.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") final long id,
                           @RequestBody final ItemDto itemDto) {
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
    public List<ListItemDto> getItems(@RequestHeader("X-Sharer-User-Id") final long id,
                                      @RequestParam(defaultValue = "0") final int from,
                                      @RequestParam(defaultValue = "100") final int size) {
        return itemService.getItems(id, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") final long id, @PathVariable final long itemId) {
        itemService.deleteItem(id, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam final String text,
                                    @RequestParam(defaultValue = "0") final int from,
                                    @RequestParam(defaultValue = "100") final int size) {
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") final long id,
                                 @PathVariable final long itemId,
                                 @RequestBody final CommentDto commentDto) {
        return itemService.addComment(id, itemId, commentDto);
    }
}


