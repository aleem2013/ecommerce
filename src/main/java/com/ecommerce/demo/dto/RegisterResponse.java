package com.ecommerce.demo.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private String name;
    private String email;
    private Set<String> roles;
    private String message;

    public static RegisterResponse fromUserDto(UserDto userDto) {
        return RegisterResponse.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .roles(userDto.getRoles())
                .message("User registered successfully")
                .build();
    }
}