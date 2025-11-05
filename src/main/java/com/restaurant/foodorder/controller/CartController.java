package com.restaurant.foodorder.controller;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.restaurant.foodorder.auth.model.AppUser;
import com.restaurant.foodorder.auth.repo.AppUserRepository;
import com.restaurant.foodorder.dto.CartDTO;
import com.restaurant.foodorder.model.temp_redis.TempCartItem;
import com.restaurant.foodorder.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final AppUserRepository appUserRepository;
    private final CartService cartService;

    public CartController(AppUserRepository appUserRepository, CartService cartService) {
        this.appUserRepository = appUserRepository;
        this.cartService = cartService;
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<?> saveCart(@RequestBody CartDTO cartDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<AppUser> user = appUserRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }
        return ResponseEntity.ok(cartService.saveCart(user.get().getUserId(), cartDTO));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<?> addToCart(@RequestBody TempCartItem tempCartItem) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<AppUser> user = appUserRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }
        return ResponseEntity.ok(cartService.addToCart(user.get().getUserId(), tempCartItem));
    }

    @GetMapping("/view")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<?> getCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<AppUser> user = appUserRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }
        return ResponseEntity.ok(cartService.getCart(user.get().getUserId()));
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<?> clearCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        Optional<AppUser> user = appUserRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }
        return ResponseEntity.ok(cartService.saveCart(user.get().getUserId(), new CartDTO(new ArrayList<>())));
    }

}
