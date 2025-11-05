package com.restaurant.foodorder.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {

    private SecretKey secretKey;
    private static long EXPIRATION_TIME = 3600000L;

    public JwtUtil() {
        String secretString = "e4e10a45e7cf8b44c42d1e914d873322fd14eb17806326729c4f1d6f2226a84e05b581f0a3fb6e36a0056ef4cd438760d864662ff757972ff9c7c23e7a5c433536b5295a3c0ac8c73be2f40a43385de8e2a34b57c7363f1ce3a9a899b7a3cb6ebc5e3c7329c88997275824192c5555f1466f9a4e2f72d9127937dd43c88b31e5249c60e2af8edfcf04e8b50ce5039e7fe58f9858183d7f28d537710c2c8cf5105a345b6eb66970b820996b66e49bb6a31c275d0e9083993e31c3eb286fb0bd1c6d81a9918778b4ea5d2c70bce5fb63c33afd1b110abef6dcc6fa3ef20968c1828c6836a72928ad714c42f20ec18d721da7d211a5b9138a6511deac8615002701ffb07c0d9c8d884cb6d068375e08eb5b189164c07cbdaf7bc0da9c5df562e2ace03633fb3e48a8025ecdf30f5f46941325995cc540d6d347c774b5cbc4a0ba8cf98118303bac5fd370498c060f699bed6c7eb0a20a0662981ba17c6dea31756c43748bf2baaa6724e61bc996ed3ad87c644421ee62a4f66d6ce189f25bfc361da24fb161f8194708064fa6549daf90fbfe400bb52d0ba0c2a54bff7ef5a935a77a101975f541bb6e841d4d4dd36da5c926b528b49aa2d9d99447de356cb639bb92b88c853f47db7de8e2ceee829175a1d2191bbc2a414a0562a78800d8fb3700c8e6ea458e3f1bbe71cb88861e97ab9e9442bd65f376f6963ff4f2827c8670e5";
        byte[] secretBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.secretKey = new SecretKeySpec(secretBytes, "HMACSHA256");
    }

    // Tạo token chứa các thông tin của user
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities()) // thêm roles vào claim để kiểm tra quyền khi gửi request
                // thêm các claim khác nếu muốn...
                .issuedAt(new Date(System.currentTimeMillis())) // thời gian tạo token
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // thời gian hết hạn của token
                .signWith(secretKey)
                .compact();
    }

    // Tạo refreshToken
    public String generateRefreshToken(HashMap<String, Objects> claims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME * 12)) // refreshToken hết hạn sau 2
                                                                                         // lần expirationTime
                .signWith(secretKey)
                .compact();
    }

    // Giải mã token để lấy thông tin user
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Giải mã claim từ token
    @SuppressWarnings("deprecation")
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(Jwts.parser().verifyWith(secretKey).build().parseClaimsJws(token).getPayload());
    }

    // Kiểm tra token có hết hạn hay không
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // Kiểm tra token có hợp lệ hay không
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
