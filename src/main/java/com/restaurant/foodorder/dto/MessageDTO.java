package com.restaurant.foodorder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageDTO {
    private String sender;
    private String content;
    // Ngày giờ gửi
    private String timestamp;
}
