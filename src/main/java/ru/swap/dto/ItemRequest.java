package ru.swap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    private Long itemId;
    private String itemName;
    private String description;
    private String section;
    private MultipartFile image;
}
