package com.ecommerce.demo.mapper;

import org.springframework.stereotype.Component;
import com.ecommerce.demo.dto.UserDto;
import com.ecommerce.demo.model.User;

@Component
public class UserMapper {
    
    public User toEntity(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .roles(userDto.getRoles())
                .build();
    }
    
    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        return dto;
    }
}
