package com.ecommerce.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMessage {
   
   @NotBlank(message = "Recipient email is required")
   @Email(message = "Invalid email format")
   private String to;

   @NotBlank(message = "Subject is required")
   private String subject;

   @NotBlank(message = "Message content is required")
   private String message;

   @JsonInclude(JsonInclude.Include.NON_NULL)
   private NotificationType type;

   private Map<String, Object> metadata;

   @Builder.Default
   private LocalDateTime timestamp = LocalDateTime.now();

   public enum NotificationType {
       EMAIL,
       SMS,
       PUSH
   }

   public void addMetadata(String key, Object value) {
       if (metadata == null) {
           metadata = new HashMap<>();
       }
       metadata.put(key, value);
   }
}
