// package com.restaurant.foodorder.real_time_test;

// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.handler.annotation.SendTo;
// import org.springframework.stereotype.Controller;
// import lombok.extern.slf4j.Slf4j;

// @Controller
// @Slf4j
// public class MessageController {

// @MessageMapping("/chat") // client gửi tới /app/chat
// @SendTo("/topic/messages") // gửi tới tất cả client đang sub /topic/messages
// public Message send(Message message) {
// log.info("Received message: {}", message);
// return message;
// }
// }
