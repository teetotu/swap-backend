package ru.swap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponse {
    private Long id;
    private String itemName;
    private String description;
    private String username;
    private String section;
    private String base64Image;
    private String city;
}
