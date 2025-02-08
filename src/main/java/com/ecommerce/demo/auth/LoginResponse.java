package com.ecommerce.demo.auth;

import java.util.List;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private List<String> roles;
}
