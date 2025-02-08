package com.ecommerce.demo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @Pattern(regexp = "^[0-9]{16}$", message = "Invalid card number")
    private String cardNumber;

    @Pattern(regexp = "^(0[1-9]|1[0-2])/([0-9]{2})$", message = "Invalid expiry date (MM/YY)")
    private String expiryDate;

    @Pattern(regexp = "^[0-9]{3,4}$", message = "Invalid CVV")
    private String cvv;
}
