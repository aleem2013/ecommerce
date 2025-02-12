package com.ecommerce.demo.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.demo.repository.UserRepository;
import com.ecommerce.demo.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        log.info("Found user: {} with roles: {}", email, user.getRoles());

        // Convert the roles from user_roles table to Spring Security authorities
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(
                role.startsWith("ROLE_") ? role : "ROLE_" + role))
            .collect(Collectors.toList());
        
        log.info("Mapped authorities: {}", authorities);

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .authorities(authorities)
            .build();
            
        // return org.springframework.security.core.userdetails.User.builder()
        //     .username(user.getEmail())
        //     .password(user.getPassword())
        //     .roles(user.getRoles().toArray(new String[0]))
        //     .build();
    }
}
