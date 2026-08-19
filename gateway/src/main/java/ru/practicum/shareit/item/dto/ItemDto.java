package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Item name can not be empty")
    private String name;
    @NotBlank(message = "Item description can not be empty")
    private String description;
    @NotNull(message = "Item availability can not be empty")
    private Boolean available;
    private Long requestId;
}
