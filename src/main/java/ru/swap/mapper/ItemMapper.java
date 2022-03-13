package ru.swap.mapper;

import org.apache.tomcat.util.codec.binary.Base64;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.swap.dto.ItemRequest;
import ru.swap.dto.ItemResponse;
import ru.swap.model.Item;
import ru.swap.model.User;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

@Mapper(componentModel = "spring")
public abstract class ItemMapper {
    @Mapping(target = "description", source = "itemRequest.description")
    @Mapping(target = "itemName", source = "itemRequest.itemName")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "city", source = "user.city")
    public abstract Item map(ItemRequest itemRequest, User user);

    @Mapping(target = "id", source = "item.itemId")
    @Mapping(target = "itemName", source = "item.itemName")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "city", source = "item.city")
    @Mapping(target = "base64Image", expression = "java(convertBytesToBase64(item.image))")
    public abstract ItemResponse mapToDto(Item item);

    public String convertBytesToBase64(byte[] bytes) {
        String res = new String(Base64.encodeBase64(bytes));
        byte[] bytess = Base64.decodeBase64(res);
        try {
            FileUtils.writeByteArrayToFile(new File("pathname1.jpg"), bytes);
            FileUtils.writeByteArrayToFile(new File("pathname2.jpg"), bytess);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}