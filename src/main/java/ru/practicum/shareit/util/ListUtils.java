package ru.practicum.shareit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListUtils {
    public static <T> List<T> concat(final List<T> left, final List<T> right) {
        return Stream.concat(left.stream(), right.stream()).collect(Collectors.toList());
    }

    public static <T> List<T> mutableListOf(final T element) {
        List<T> list = new ArrayList<>();
        list.add(element);

        return list;
    }
}
