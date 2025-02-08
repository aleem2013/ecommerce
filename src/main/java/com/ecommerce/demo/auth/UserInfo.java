package com.ecommerce.demo.auth;

import java.util.List;

import lombok.Data;

@Data
public class UserInfo {
    private String email;
    private String name;
    private List<String> roles;
}
