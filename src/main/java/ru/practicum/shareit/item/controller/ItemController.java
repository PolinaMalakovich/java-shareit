package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
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
    public List<ListItemDto> getItems(@RequestHeader("X-Sharer-User-Id") final long id,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                      @RequestParam(defaultValue = "100") @Positive final int size) {
        return itemService.getItems(id, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") final long id, @PathVariable final long itemId) {
        itemService.deleteItem(id, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam final String text,
                                    @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                    @RequestParam(defaultValue = "100") @Positive final int size) {
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") final long id,
                                 @PathVariable final long itemId,
                                 @Valid @RequestBody final CommentDto commentDto) {
        return itemService.addComment(id, itemId, commentDto);
    }
}


