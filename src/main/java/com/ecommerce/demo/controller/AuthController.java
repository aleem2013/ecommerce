package com.ecommerce.demo.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.demo.auth.LoginRequest;
import com.ecommerce.demo.auth.LoginResponse;
import com.ecommerce.demo.auth.UserInfo;
import com.ecommerce.demo.dto.RegisterRequest;
import com.ecommerce.demo.dto.UserDto;
import com.ecommerce.demo.service.JwtTokenProvider;
import com.ecommerce.demo.service.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.FieldError;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    
    public AuthController(AuthenticationManager authenticationManager,
                         JwtTokenProvider tokenProvider,
                         UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        LoginResponse response = new LoginResponse();
        response.setToken(jwt);
        response.setEmail(userDetails.getUsername());
        response.setRoles(roles);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<UserInfo> getUserInfo(@AuthenticationPrincipal JwtAuthenticationToken principal) {
        Map<String, Object> claims = principal.getTokenAttributes();
        String email = claims.get("email").toString();

         // Utilize userService to get additional user details
        UserDto userDto = userService.getUserByEmail(email);
        
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(claims.get("email").toString());
        userInfo.setName(claims.get("name").toString());
        userInfo.setRoles(extractRoles(claims));
        
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        UserDto userDto = UserDto.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(request.getPassword())
            .build();
        log.info("******** Request details {}", userDto.toString());
        
        UserDto createdUser = userService.createUser(userDto);
        log.info("*************** User registered *********** {}", createdUser.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    private List<String> extractRoles(Map<String, Object> claims) {
        try {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("realm_access.roles");
            return roles != null ? roles : Collections.emptyList();
        } catch (Exception e) {
            log.warn("Could not extract roles from token", e);
            return Collections.emptyList();
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
