package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") final long id,
                                     @Valid @RequestBody final ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(id, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoWithAnswers> getRequests(@RequestHeader("X-Sharer-User-Id") final long id) {
        return itemRequestService.getRequests(id);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithAnswers> getAllRequests(
        @RequestHeader("X-Sharer-User-Id") final long id,
        @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
        @RequestParam(defaultValue = "100") @Positive final int size
    ) {
        return itemRequestService.getAllRequests(id, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithAnswers getRequest(@RequestHeader("X-Sharer-User-Id") final long id,
                                                @PathVariable final long requestId) {
        return itemRequestService.getRequest(id, requestId);
    }
}
