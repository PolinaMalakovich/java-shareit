package ru.practicum.shareit.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilsTest {

    private static Stream<Arguments> suffixArguments() {
        return Stream.of(
            Arguments.of("", '.', ""),
            Arguments.of(".", '.', ""),
            Arguments.of("foo.bar", '.', "bar"),
            Arguments.of("foo.bar.baz", '.', "baz")
        );
    }

    @ParameterizedTest
    @MethodSource("suffixArguments")
    void suffix(String line, char character, String expected) {
        assertEquals(expected, StringUtils.suffix(line, character));
    }
}