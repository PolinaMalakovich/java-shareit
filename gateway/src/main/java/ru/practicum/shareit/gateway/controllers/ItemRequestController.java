package ru.practicum.shareit.gateway.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.gateway.ShareItClient;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ShareItClient shareItClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") final long id,
                                             @Valid @RequestBody final ItemRequestDto itemRequestDto) {
        return shareItClient.post("/requests", id, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") final long id) {
        return shareItClient.get("/requests", id);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
        @RequestHeader("X-Sharer-User-Id") final long id,
        @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
        @RequestParam(defaultValue = "100") @Positive final int size
    ) {
        return shareItClient.get(
            "/requests/all?from={from}&size={size}",
            id,
            Map.ofEntries(
                Map.entry("from", from),
                Map.entry("size", size)
            )
        );
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") final long id,
                                                @PathVariable final long requestId) {
        return shareItClient.get("/requests/" + requestId, id);
    }
}
