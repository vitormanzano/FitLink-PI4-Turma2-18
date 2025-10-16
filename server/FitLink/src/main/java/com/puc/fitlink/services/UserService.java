package com.puc.fitlink.services;

import com.puc.fitlink.converter.UserConverter;
import com.puc.fitlink.converter.UserMapper;
import com.puc.fitlink.dto.request.UserRequestDto;
import com.puc.fitlink.dto.response.UserResponseDto;
import com.puc.fitlink.models.User;
import com.puc.fitlink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static org.springframework.util.Assert.notNull;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final UserMapper userMapper;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public UserResponseDto signUpUser(UserRequestDto userRequestDto) throws Exception {
        try {
            notNull(userRequestDto, "Os dados do usuário são obrigatórios");

            User user = saveUser(userConverter.ToUserModel(userRequestDto));
            return userMapper.toUserResponseDto(user);
        } catch (Exception e) {
            throw new Exception("Erro ao gravar dados de usuário", e);
        }
    }


}
