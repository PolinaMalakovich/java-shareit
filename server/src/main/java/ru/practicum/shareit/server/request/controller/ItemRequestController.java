package ru.practicum.shareit.server.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.dto.request.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.server.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") final long id,
                                     @RequestBody final ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(id, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoWithAnswers> getRequests(@RequestHeader("X-Sharer-User-Id") final long id) {
        return itemRequestService.getRequests(id);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithAnswers> getAllRequests(
        @RequestHeader("X-Sharer-User-Id") final long id,
        @RequestParam(defaultValue = "0") final int from,
        @RequestParam(defaultValue = "100") final int size
    ) {
        return itemRequestService.getAllRequests(id, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithAnswers getRequest(@RequestHeader("X-Sharer-User-Id") final long id,
                                                @PathVariable final long requestId) {
        return itemRequestService.getRequest(id, requestId);
    }
}
