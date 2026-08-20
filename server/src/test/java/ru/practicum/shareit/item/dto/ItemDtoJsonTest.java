package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDtoShort;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.VarsTestList.*;


@JsonTest
public class ItemDtoJsonTest {


    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    @DisplayName("Serialize item dto")
    public void serialize_itemDto_containsExpectedFields() throws Exception {
        ItemDto itemDto = new ItemDto(1L, VALID_ITEM_NAME_1, VALID_ITEM_DESCRIPTION_1, true, 3L);
        itemDto.setLastBooking(new BookingDtoShort(10L, 2L, LocalDateTime.of(2026, 5, 10, 12, 0)));
        itemDto.setNextBooking(new BookingDtoShort(11L, 2L, LocalDateTime.of(2026, 5, 12, 12, 0)));
        itemDto.setComments(List.of(new CommentDto(1L, VALID_COMMENT_TEXT, "Victor", LocalDateTime.of(2026, 8, 20, 0, 0))));

        assertThat(json.write(itemDto)).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json.write(itemDto)).extractingJsonPathStringValue("$.name").isEqualTo(VALID_ITEM_NAME_1);
        assertThat(json.write(itemDto)).extractingJsonPathNumberValue("$.requestId").isEqualTo(3);
        assertThat(json.write(itemDto)).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(10);
        assertThat(json.write(itemDto)).extractingJsonPathStringValue("$.comments[0].text").isEqualTo(VALID_COMMENT_TEXT);
    }
}
