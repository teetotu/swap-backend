package ru.swap.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import ru.swap.dto.ItemRequest;
import ru.swap.dto.ItemResponse;
import ru.swap.exceptions.ItemNotFoundException;
import ru.swap.exceptions.SwapApplicationException;
import ru.swap.mapper.ItemMapper;
import ru.swap.model.Item;
import ru.swap.model.Section;
import ru.swap.model.User;
import ru.swap.repository.ItemRepository;
import ru.swap.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final ItemMapper itemMapper;

    public void save(ItemRequest itemRequest) throws IOException {
        Section section = Section.findByKey(itemRequest.getSection());
        if (section.equals(Section.NOT_FOUND)) throw new SwapApplicationException("Section does not exist");
        Item item = itemMapper.map(itemRequest, authService.getCurrentUser());
        item.setSection(section);
        item.setImage(itemRequest.getImage().getBytes());
        itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public ItemResponse getItem(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id.toString()));
        return itemMapper.mapToDto(item);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getAllItems() {
        itemRepository.findAll().forEach(item -> log.info("DEBUG " + item.itemName));
        return itemRepository.findAll()
                .stream()
                .map(itemMapper::mapToDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getItemsBySection(String section) {
        List<Item> items = itemRepository.findAllBySection(Section.findByKey(section));
        return items.stream().map(itemMapper::mapToDto).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getItemsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return itemRepository.findByUser(user)
                .stream()
                .map(itemMapper::mapToDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getItemsByKeywords(String keywords) {
        return itemRepository
                .search(keywords)
                .stream()
                .map(itemMapper::mapToDto)
                .collect(toList());
    }

    public void updateItem(ItemRequest itemRequest) {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Item item = itemRepository.findById(itemRequest.getItemId()).orElseThrow(() -> new SwapApplicationException("Item not found"));;
        if (Objects.equals(item.getUser().getUsername(), principal.getSubject())
                && authService.isSessionActive(principal.getSubject())) {
            itemRepository.findById(itemRequest.getItemId())
                    .map(it -> {
                        if (itemRequest.getImage() != null) {
                            try {
                                it.setImage(itemRequest.getImage().getBytes());
                            } catch (IOException e) {
                                log.error(e.getMessage());
                                return itemRepository.save(it);
                            }
                        }
                        if (itemRequest.getItemName() != null) it.setItemName(itemRequest.getItemName());
                        if (itemRequest.getSection() != null)
                            it.setSection(Section.findByKey(itemRequest.getSection()));
                        if (itemRequest.getDescription() != null) it.setDescription(itemRequest.getDescription());
                        return itemRepository.save(it);
                    });
        } else {
            throw new SwapApplicationException("Unauthorized access");
        }
    }

    public void deleteItemById(Long id) {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Item item = itemRepository.findById(id).orElseThrow(() -> new SwapApplicationException("Item not found"));;
        if (Objects.equals(item.getUser().getUsername(), principal.getSubject())
                && authService.isSessionActive(principal.getSubject())) {
            itemRepository.deleteById(id);
        } else {
            throw new SwapApplicationException("Unauthorized access");
        }
    }
}
