package com.puc.fitlink.controller;

import com.puc.fitlink.dto.request.UserLoginDto;
import com.puc.fitlink.dto.request.UserRequestDto;
import com.puc.fitlink.dto.response.UserResponseDto;
import com.puc.fitlink.exceptions.User.UserAlreadyExistException;
import com.puc.fitlink.exceptions.User.UserNotFoundException;
import com.puc.fitlink.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserResponseDto> signUpUser(@RequestBody UserRequestDto userRequestDto) {
        try {
            return ResponseEntity.status(201).body(userService.signUpUser(userRequestDto));
        }
        catch (UserAlreadyExistException e) {
            return ResponseEntity.status(409).body(null);
        }
        catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> loginUser(@RequestBody UserLoginDto userLoginDto) {
        try {
            return ResponseEntity.status(200).body(userService.login(userLoginDto));
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
        catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

}
