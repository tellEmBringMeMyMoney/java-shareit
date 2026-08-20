package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void equals_sameObject_returnsTrue() {
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@test.ru")
                .build();

        assertEquals(user, user);
    }

    @Test
    void equals_null_returnsFalse() {
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@test.ru")
                .build();

        assertNotEquals(user, null);
    }

    @Test
    void equals_differentClass_returnsFalse() {
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@test.ru")
                .build();

        assertNotEquals(user, "not a user");
    }

    @Test
    void equals_sameId_returnsTrue() {
        User user1 = User.builder()
                .id(1L)
                .name("User 1")
                .email("user1@test.ru")
                .build();

        User user2 = User.builder()
                .id(1L)
                .name("User 2")
                .email("user2@test.ru")
                .build();

        assertEquals(user1, user2);
    }

    @Test
    void equals_differentId_returnsFalse() {
        User user1 = User.builder()
                .id(1L)
                .name("User")
                .email("user1@test.ru")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("User")
                .email("user2@test.ru")
                .build();

        assertNotEquals(user1, user2);
    }

    @Test
    void equals_nullId_returnsFalse() {
        User user1 = User.builder()
                .name("User")
                .email("user1@test.ru")
                .build();

        User user2 = User.builder()
                .name("User")
                .email("user1@test.ru")
                .build();

        assertNotEquals(user1, user2);
    }

    @Test
    void hashCode_returnsClassHashCode() {
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@test.ru")
                .build();

        assertEquals(User.class.hashCode(), user.hashCode());
    }
}
