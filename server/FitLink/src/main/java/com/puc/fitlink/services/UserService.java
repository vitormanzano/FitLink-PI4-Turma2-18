package com.puc.fitlink.services;

import com.puc.fitlink.converter.UserConverter;
import com.puc.fitlink.converter.UserMapper;
import com.puc.fitlink.dto.request.UserLoginDto;
import com.puc.fitlink.dto.request.UserRequestDto;
import com.puc.fitlink.dto.response.UserResponseDto;
import com.puc.fitlink.exceptions.User.UserAlreadyExistException;
import com.puc.fitlink.exceptions.User.UserNotFoundException;
import com.puc.fitlink.models.User;
import com.puc.fitlink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.springframework.util.Assert.notNull;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public UserResponseDto signUpUser(UserRequestDto userRequestDto) throws Exception {
        notNull(userRequestDto, "Os dados do usuário são obrigatórios");

        User userExist = userRepository.findByEmail(userRequestDto.email());
        if (userExist != null)
            throw new UserAlreadyExistException("Usuário com esse email já existe!");
        String encodedPassword = passwordEncoder.encode(userRequestDto.password());
        UserRequestDto encodedRequest = new UserRequestDto(
                userRequestDto.name(),
                userRequestDto.email(),
                encodedPassword
        );
        
        User user = saveUser(userConverter.ToUserModel(userRequestDto));
        return userMapper.toUserResponseDto(user);
    }

    public UserResponseDto login(UserLoginDto userLoginDto) throws Exception {
        notNull(userLoginDto, "Os dados do usuário são obrigatórios");

        User user = userRepository.findByEmail(userLoginDto.email());
        if (user == null)
            throw new UserNotFoundException("Usuário com esse email não encontrado!");

        if (passwordEncoder.matches(userLoginDto.password(), user.getPasswordHash()))
            throw new Exception("Senha incorreta!");

        return userMapper.toUserResponseDto(user);
    }
}
