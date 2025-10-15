package com.puc.fitlink.controller;

import com.puc.fitlink.dto.request.UserRequestDto;
import com.puc.fitlink.dto.response.UserResponseDto;
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
    public ResponseEntity<UserResponseDto> signUpUser(@RequestBody UserRequestDto userRequestDto) throws Exception {
        return ResponseEntity.ok(userService.signUpUser(userRequestDto));
    }

}
