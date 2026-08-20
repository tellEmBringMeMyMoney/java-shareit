package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    @Test
    void shouldCreateRequestUsingBuilderAndGetters() {
        LocalDateTime created = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Need a pickaxe")
                .created(created)
                .build();

        assertEquals(1L, request.getId());
        assertEquals("Need a pickaxe", request.getDescription());
        assertEquals(created, request.getCreated());
    }

    @Test
    void shouldSetRequestFields() {
        LocalDateTime created = LocalDateTime.now();

        ItemRequest request = new ItemRequest();

        request.setId(1L);
        request.setDescription("Need a pickaxe");
        request.setCreated(created);

        assertEquals(1L, request.getId());
        assertEquals("Need a pickaxe", request.getDescription());
        assertEquals(created, request.getCreated());
    }

    @Test
    void shouldBeEqualToItself() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .build();

        assertEquals(request, request);
    }

    @Test
    void shouldNotBeEqualToNull() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .build();

        assertNotEquals(null, request);
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        ItemRequest first = ItemRequest.builder()
                .id(1L)
                .description("First")
                .build();

        ItemRequest second = ItemRequest.builder()
                .id(1L)
                .description("Second")
                .build();

        assertEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        ItemRequest first = ItemRequest.builder().id(1L).build();
        ItemRequest second = ItemRequest.builder().id(2L).build();

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenIdIsNull() {
        ItemRequest first = ItemRequest.builder().id(null).build();
        ItemRequest second = ItemRequest.builder().id(null).build();

        assertNotEquals(first, second);
    }

    @Test
    void shouldReturnSameHashCodeForRequestsOfSameClass() {
        ItemRequest first = new ItemRequest();
        ItemRequest second = new ItemRequest();

        assertEquals(first.hashCode(), second.hashCode());
    }
}