package ru.swap.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import ru.swap.SwapApplication;
import ru.swap.dto.ItemRequest;
import ru.swap.dto.ItemResponse;
import ru.swap.exceptions.SwapApplicationException;
import ru.swap.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/items/")
@AllArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> createItem(@ModelAttribute ItemRequest itemRequest) {
        try {
            itemService.save(itemRequest);
        } catch (SwapApplicationException e) {
            return new ResponseEntity<>("Section not found.", HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (IOException e) {
            return new ResponseEntity<>("File upload error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Successfully added item", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        return status(HttpStatus.OK).body(itemService.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getItem(@PathVariable("id") Long id) {
        return status(HttpStatus.OK).body(itemService.getItem(id));
    }

    @GetMapping("bySection/{section}")
    public ResponseEntity<List<ItemResponse>> getItemsBySection(@PathVariable("section") String section) {
        return status(HttpStatus.OK).body(itemService.getItemsBySection(section));
    }

    @GetMapping("byUser/{username}")
    public ResponseEntity<List<ItemResponse>> getItemsByUsername(@PathVariable("username") String username) {
        return status(HttpStatus.OK).body(itemService.getItemsByUsername(username));
    }

    @GetMapping("by-title/{keywords}")
    public ResponseEntity<List<ItemResponse>> getItemsByKeywords(@PathVariable("keywords") String keywords) {
        return status(HttpStatus.OK).body(itemService.getItemsByKeywords(keywords));
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateItem(@ModelAttribute ItemRequest itemRequest) {
        itemService.updateItem(itemRequest);
        return status(HttpStatus.NO_CONTENT).body("updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable("id") Long id) {
        itemService.deleteItemById(id);
        return status(HttpStatus.OK).body("Deleted successfully");
    }
}
