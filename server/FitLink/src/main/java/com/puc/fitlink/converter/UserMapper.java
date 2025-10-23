package com.puc.fitlink.converter;

import com.puc.fitlink.dto.response.UserResponseDto;
import com.puc.fitlink.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "email", source = "user.email")
    UserResponseDto toUserResponseDto(User user);
}
