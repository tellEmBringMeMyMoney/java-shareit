package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void shouldCreateItemUsingBuilderAndGetters() {
        Item item = Item.builder()
                .id(1L)
                .name("Pickaxe")
                .description("Stone Pickaxe")
                .available(true)
                .build();

        assertEquals(1L, item.getId());
        assertEquals("Pickaxe", item.getName());
        assertEquals("Stone Pickaxe", item.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    void shouldSetItemFields() {
        Item item = new Item();

        item.setId(1L);
        item.setName("Pickaxe");
        item.setDescription("Stone Pickaxe");
        item.setAvailable(true);

        assertEquals(1L, item.getId());
        assertEquals("Pickaxe", item.getName());
        assertEquals("Stone Pickaxe", item.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    void shouldBeEqualToItself() {
        Item item = Item.builder()
                .id(1L)
                .name("Pickaxe")
                .build();

        assertEquals(item, item);
    }

    @Test
    void shouldNotBeEqualToNull() {
        Item item = Item.builder()
                .id(1L)
                .build();

        assertNotEquals(null, item);
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        Item first = Item.builder()
                .id(1L)
                .name("Pickaxe")
                .build();

        Item second = Item.builder()
                .id(1L)
                .name("Another Pickaxe")
                .build();

        assertEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        Item first = Item.builder()
                .id(1L)
                .build();

        Item second = Item.builder()
                .id(2L)
                .build();

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenIdIsNull() {
        Item first = Item.builder()
                .id(null)
                .build();

        Item second = Item.builder()
                .id(null)
                .build();

        assertNotEquals(first, second);
    }

    @Test
    void shouldReturnSameHashCodeForItemsOfSameClass() {
        Item first = new Item();
        Item second = new Item();

        assertEquals(first.hashCode(), second.hashCode());
    }
}
