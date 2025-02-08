package com.ecommerce.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.demo.dto.UserDto;
import com.ecommerce.demo.exception.EmailAlreadyExistsException;
import com.ecommerce.demo.exception.UserNotFoundException;
import com.ecommerce.demo.mapper.UserMapper;
import com.ecommerce.demo.model.User;
import com.ecommerce.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + userDto.getEmail());
        }
        
        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
         // Initialize roles if null
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        // Add ROLE_USER if roles is empty
        if (user.getRoles().isEmpty()) {
            user.getRoles().add("ROLE_USER");
        }
        
        // Ensure roles are attached to the persistence context
        User savedUser = userRepository.save(user);
        userRepository.flush();  // Force immediate persistence
        
        return userMapper.toDto(savedUser);
    }
    
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return userMapper.toDto(user);
    }
}
