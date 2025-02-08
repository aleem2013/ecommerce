package com.ecommerce.demo.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class JsonAttributeConverter implements AttributeConverter<Map<String, Object>, String> {
   private static final ObjectMapper objectMapper = new ObjectMapper();

   @Override
   public String convertToDatabaseColumn(Map<String, Object> attribute) {
       try {
           return objectMapper.writeValueAsString(attribute);
       } catch (JsonProcessingException e) {
           throw new IllegalArgumentException("Error converting map to JSON", e);
       }
   }

   @Override
   public Map<String, Object> convertToEntityAttribute(String dbData) {
       try {
           if (dbData == null) {
               return new HashMap<>();
           }
           return objectMapper.readValue(dbData, new TypeReference<Map<String, Object>>() {});
       } catch (JsonProcessingException e) {
           throw new IllegalArgumentException("Error converting JSON to map", e);
       }
   }
}
