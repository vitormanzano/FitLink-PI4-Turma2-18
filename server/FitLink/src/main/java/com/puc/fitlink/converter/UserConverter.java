package com.puc.fitlink.converter;

import com.puc.fitlink.dto.request.UserRequestDto;
import com.puc.fitlink.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserConverter {
    public User ToUserModel(UserRequestDto userRequestDto) {
        return User.builder()
                .id(UUID.randomUUID().toString())
                .name(userRequestDto.name())
                .email(userRequestDto.email())
                .password(userRequestDto.password())
                .build();
    }

}
