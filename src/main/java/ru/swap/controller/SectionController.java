package ru.swap.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.swap.model.Section;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/sections/")
@AllArgsConstructor
public class SectionController {

    @GetMapping
    public ResponseEntity<List<String>> getSections() {
        return status(HttpStatus.OK).body(
                Arrays.stream(Section.values())
                        .map(Section::getSectionDescription)
                        .collect(Collectors.toList()));
    }
}
