package ru.practicum.shareit.gateway.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.item.*;
import ru.practicum.shareit.gateway.ShareItClient;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ShareItClient shareItClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") final long id,
                                          @Valid @RequestBody final ItemDto itemDto) {
        return shareItClient.post("/items", id, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") final long id,
                              @PathVariable final long itemId,
                              @RequestBody final PatchItemDto patchItemDto) {
        return shareItClient.patch("/items/" + itemId, id, patchItemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") final long id,
                                          @PathVariable final long itemId) {
        return shareItClient.get("/items/" + itemId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") final long id,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                      @RequestParam(defaultValue = "100") @Positive final int size) {
        return shareItClient.get(
            "/items?from={from}&size={size}",
            id,
            Map.ofEntries(
                Map.entry("from", from),
                Map.entry("size", size)
            )
        );
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") final long id, @PathVariable final long itemId) {
        shareItClient.delete("/items/" + itemId, id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam final String text,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                             @RequestParam(defaultValue = "100") @Positive final int size) {
        return shareItClient.get(
            "/items/search?text={text}&from={from}&size={size}",
            Map.ofEntries(
                Map.entry("text", text),
                Map.entry("from", from),
                Map.entry("size", size)
            )
        );
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") final long id,
                                 @PathVariable final long itemId,
                                 @Valid @RequestBody final CommentDto commentDto) {
        return shareItClient.post("/items/" + itemId + "/comment", id, commentDto);
    }
}


