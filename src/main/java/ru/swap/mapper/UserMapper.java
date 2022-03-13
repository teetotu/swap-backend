package ru.swap.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.swap.dto.UserResponse;
import ru.swap.model.User;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "contactInfo", source = "user.contactInfo")
    public abstract UserResponse mapToDto(User user);
}