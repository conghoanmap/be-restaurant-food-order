package com.restaurant.foodorder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Loại bỏ các trường null khi trả về response
@JsonIgnoreProperties(ignoreUnknown = true) // Bỏ qua các trường không cần thiết khi nhận response
public class APIResponse<T> {
    private int statusCode;
    private String message;
    private T data;
}