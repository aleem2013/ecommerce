package com.ecommerce.demo.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @NotBlank
    @Email
    @Size(max = 100)
    private String email;
    
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  // Only deserialize, never serialize
    @Size(min = 8, max = 100)
    private String password;
    
    private Set<String> roles;
}
