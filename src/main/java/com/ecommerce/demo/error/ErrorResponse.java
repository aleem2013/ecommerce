package com.ecommerce.demo.error;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(String message) {
        this.status = HttpStatus.NOT_FOUND.value();
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}