package com.restaurant.foodorder.auth.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.restaurant.foodorder.auth.JwtUtil;
import com.restaurant.foodorder.auth.dto.LogRegResponse;
import com.restaurant.foodorder.auth.dto.LoginRequest;
import com.restaurant.foodorder.auth.dto.RegisterRequest;
import com.restaurant.foodorder.auth.model.AppUser;
import com.restaurant.foodorder.auth.model.Role;
import com.restaurant.foodorder.auth.repo.AppUserRepository;
import com.restaurant.foodorder.dto.APIResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

    private final AppUserService appUserService;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserService(AppUserService appUserService, AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.appUserService = appUserService;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public APIResponse<?> register(RegisterRequest registerRequest) {
        // Kiểm tra email đã tồn tại chưa
        Optional<AppUser> user = appUserRepository.findByEmail(registerRequest.getEmail());
        if (user.isPresent()) {
            return new APIResponse<>(400, "Email is already taken", null);
        }
        // Kiểm tra password và confirmPassword có trùng nhau không
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return new APIResponse<>(400, "Password and Confirm Password do not match", null);
        }

        // Lưu thông tin user vào database
        AppUser newUser = new AppUser();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setFullName(registerRequest.getFullName());
        newUser.setRoles(Set.of(new Role("ROLE_CUSTOMER")));

        AppUser createdUser = appUserRepository.save(newUser);
        log.info("New user has been registered: {}", createdUser.getEmail());
        return new APIResponse<>(200, "Register successfully", createdUser);
    }

    public APIResponse<LogRegResponse> login(LoginRequest loginRequest) {
        try {
            // Kiểm tra xem email có tồn tại không
            Optional<AppUser> appuser = appUserRepository.findByEmail(loginRequest.getEmail());
            if (appuser.isEmpty()) {
                return new APIResponse<>(400, "Email not found", null);
            }

            // Kiểm tra xem email và password có khớp không
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), loginRequest.getPassword()));

            // Tạo token
            AppUser user = appuser.get();
            String accessToken = jwtUtil.generateToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(new HashMap<>(), user);

            LogRegResponse logRegResponse = LogRegResponse.builder().accessToken(accessToken).refreshToken(refreshToken)
                    .expirationTime("Expiration time: 60 minutes").roles(
                            user.getRoles().stream().map(role -> role.getRoleName()).collect(Collectors.toSet()))
                    .build();
            log.info("User {} logged in", user.getEmail());

            return new APIResponse<>(200, "Login successfully", logRegResponse);
        } catch (Exception ex) {
            return new APIResponse<>(400, "Login failed, check your email and password again!", null);
        }
    }

    public APIResponse<String> refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails foundUser = appUserService.loadUserByUsername(username);
        boolean isTokenExpired = jwtUtil.validateToken(refreshToken, foundUser);

        if (!isTokenExpired) {
            return new APIResponse<>(400, "Refresh token is expired", null);
        }

        String newAccessToken = jwtUtil.generateToken(foundUser);
        return new APIResponse<>(200, "Refresh token successfully", newAccessToken);
    }

    public APIResponse<AppUser> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<AppUser> user = appUserRepository.findByEmail(email);
        if (user.isEmpty()) {
            return new APIResponse<>(404, "User not found", null);
        }
        AppUser foundUser = user.get();
        return new APIResponse<>(200, "Success", foundUser);
    }

    public APIResponse<List<AppUser>> getAllUsers() {
        List<AppUser> users = appUserRepository.findAll();
        return new APIResponse<>(200, "Success", users);
    }

    public APIResponse<?> updatePermission(Long userId, Set<Role> roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<AppUser> user = appUserRepository.findById(userId);
        if (user.isEmpty()) {
            return new APIResponse<>(404, "User not found", null);
        } else if (user.get().getEmail().equals(email)) {
            return new APIResponse<>(400, "You can not update your permission", null);
        }
        AppUser foundUser = user.get();
        foundUser.setRoles(roles);
        appUserRepository.save(foundUser);
        return new APIResponse<>(200, "Update permission successfully", foundUser);
    }
}
