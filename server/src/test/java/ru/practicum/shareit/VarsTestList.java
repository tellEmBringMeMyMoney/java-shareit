package ru.practicum.shareit;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

public final class VarsTestList {
    public static final long NON_EXISTING_ID = 9999L;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    public static final String VALID_USER_NAME_1 = "Victor";
    public static final String VALID_USER_NAME_2 = "Steve";
    public static final String VALID_USER_NAME_3 = "Joe";
    public static final String UPDATED_USER_NAME = "New Victor";

    public static final String VALID_EMAIL_1 = "victor@example.com";
    public static final String VALID_EMAIL_2 = "steve@example.com";
    public static final String VALID_EMAIL_3 = "joe@example.com";
    public static final String UPDATED_EMAIL = "updated@example.com";
    public static final String INVALID_EMAIL = "invalid-email";

    public static final String VALID_ITEM_NAME_1 = "Stone pickaxe";
    public static final String VALID_ITEM_NAME_2 = "Diamond hoe";
    public static final String VALID_ITEM_DESCRIPTION_1 = "Enchanted stone pickaxe";
    public static final String VALID_ITEM_DESCRIPTION_2 = "You can not put loyalty on a hoe";
    public static final String UPDATED_ITEM_NAME = "Iron pickaxe";
    public static final String UPDATED_ITEM_DESCRIPTION = "Now you can mine diamonds";
    public static final String SEARCH_TEXT_MATCH = "pickaxe";
    public static final String SEARCH_TEXT_BLANK = "   ";
    public static final String VALID_COMMENT_TEXT = "Mined 3 coal (usually it's 2). Lovely tool!";
    public static final String UPDATED_COMMENT_TEXT = "Anyways iron still OP";

    public static final UserDto VALID_USER_DTO_1 = new UserDto(null, VALID_USER_NAME_1, VALID_EMAIL_1);
    public static final UserDto VALID_USER_DTO_2 = new UserDto(null, VALID_USER_NAME_2, VALID_EMAIL_2);
    public static final UserDto VALID_USER_DTO_3 = new UserDto(null, VALID_USER_NAME_3, VALID_EMAIL_3);
    public static final UserDto INVALID_USER_DTO_NULL_NAME = new UserDto(null, null, VALID_EMAIL_1);
    public static final UserDto INVALID_USER_DTO_BLANK_NAME = new UserDto(null, "   ", VALID_EMAIL_1);
    public static final UserDto INVALID_USER_DTO_NULL_EMAIL = new UserDto(null, VALID_USER_NAME_1, null);
    public static final UserDto INVALID_USER_DTO_INVALID_EMAIL = new UserDto(null, VALID_USER_NAME_1, INVALID_EMAIL);

    public static final ItemDto VALID_ITEM_DTO_1 = new ItemDto(null, VALID_ITEM_NAME_1, VALID_ITEM_DESCRIPTION_1, true, null);
    public static final ItemDto VALID_ITEM_DTO_2 = new ItemDto(null, VALID_ITEM_NAME_2, VALID_ITEM_DESCRIPTION_2, false, null);
    public static final ItemDto INVALID_ITEM_DTO_NULL_NAME = new ItemDto(null, null, VALID_ITEM_DESCRIPTION_1, true, null);
    public static final ItemDto INVALID_ITEM_DTO_BLANK_NAME = new ItemDto(null, "   ", VALID_ITEM_DESCRIPTION_1, true, null);
    public static final ItemDto INVALID_ITEM_DTO_NULL_DESCRIPTION = new ItemDto(null, VALID_ITEM_NAME_1, null, true, null);
    public static final ItemDto INVALID_ITEM_DTO_BLANK_DESCRIPTION = new ItemDto(null, VALID_ITEM_NAME_1, "   ", true, null);
    public static final ItemDto INVALID_ITEM_DTO_NULL_AVAILABLE = new ItemDto(null, VALID_ITEM_NAME_1, VALID_ITEM_DESCRIPTION_1, null, null);
}
