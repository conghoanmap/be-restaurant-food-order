package com.restaurant.foodorder.auth;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.restaurant.foodorder.auth.dto.LoginRequest;
import com.restaurant.foodorder.auth.dto.RegisterRequest;
import com.restaurant.foodorder.auth.model.Role;
import com.restaurant.foodorder.auth.service.UserService;
import com.restaurant.foodorder.dto.APIResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(@Autowired UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        var apiResponse = userService.login(loginRequest);

        if (apiResponse.getStatusCode() != 200) {
            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
        }
        Cookie refreshTokenCookie = new Cookie("refreshToken", apiResponse.getData().getRefreshToken());
        refreshTokenCookie.setHttpOnly(true); // Không cho JS truy cập
        refreshTokenCookie.setSecure(true); // Chỉ gửi qua HTTPS (bắt buộc trên môi trường thật)
        refreshTokenCookie.setPath("/"); // Phạm vi áp dụng cookie
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày

        response.addCookie(refreshTokenCookie);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(userService.register(registerRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(401).body("Refresh token is missing");
        }

        return ResponseEntity.ok(userService.refreshToken(refreshToken));
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PutMapping("/permission")
    public ResponseEntity<?> updatePermission(@RequestParam("userId") Long userId, @RequestBody Set<Role> roles) {
        return ResponseEntity.ok(userService.updatePermission(userId, roles));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 🔥 Xóa cookie

        response.addCookie(cookie);
        return ResponseEntity.ok(new APIResponse<>(200, "Đăng xuất thành công", null));
    }
}
