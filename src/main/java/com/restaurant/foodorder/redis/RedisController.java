package com.restaurant.foodorder.redis;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/redis")
public class RedisController {
    private final RedisService redisService;

    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @GetMapping("/get")
    public ResponseEntity<Object> getData(@RequestParam String key) {
        Object value = redisService.get(key);
        if (value != null) {
            return ResponseEntity.ok(value);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveData(@RequestParam String key, @RequestParam String value,
            @RequestParam long ttlSeconds) {
        redisService.save(key, value, ttlSeconds);
        return ResponseEntity.ok("Data saved successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateData(@RequestParam String key, @RequestParam String value) {
        redisService.update(key, value);
        return ResponseEntity.ok("Data updated successfully");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteData(@RequestParam String key) {
        redisService.delete(key);
        return ResponseEntity.ok("Data deleted successfully");
    }
}
