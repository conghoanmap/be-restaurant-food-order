package com.restaurant.foodorder.payment.momo;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.restaurant.foodorder.dto.APIResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MoMoService {

    @Value("${momo.access-key}")
    private String accessKey;

    @Value("${momo.partner-code}")
    private String partnerCode;

    @Value("${momo.secret-key}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String endpoint;

    @Value("${momo.redirect-url}")
    private String redirectUrl;

    @Value("${momo.ipn-url}")
    private String ipnUrl;

    public APIResponse<MoMoResponse> createPayment(String orderId, long amount, String orderInfo, String returnUrl,
            String paymentMethod) {
        try {
            String requestId = UUID.randomUUID().toString();
            String amountt = String.valueOf(amount);
            String requestType = "captureWallet";
            switch (paymentMethod) {
                case "QR_CODE":
                    requestType = "captureWallet";
                    break;
                case "ATM":
                    requestType = "payWithATM";
                    break;

                case "CC":
                    requestType = "payWithCC";
                    break;
                default:
                    break;
            }
            // Tạo chữ ký
            String rawHash = "accessKey=" + accessKey +
                    "&amount=" + amountt +
                    "&extraData=" +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = signHmacSHA256(rawHash, secretKey);

            Map<String, Object> body = new HashMap<>();
            body.put("partnerCode", partnerCode);
            body.put("accessKey", accessKey);
            body.put("requestId", requestId);
            body.put("amount", amount);
            body.put("orderId", orderId);
            body.put("orderInfo", orderInfo);
            body.put("redirectUrl", this.redirectUrl);
            body.put("ipnUrl", this.ipnUrl);
            body.put("requestType", requestType);
            body.put("signature", signature);
            body.put("extraData", "");
            body.put("lang", "vi");

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<MoMoResponse> response = restTemplate.postForEntity(endpoint,
                    body, MoMoResponse.class);
            return new APIResponse<>(200, "Create MoMo payment successfully", response.getBody());
        } catch (Exception e) {
            log.error("Error creating MoMo payment", e);
            return new APIResponse<>(500, "Error creating MoMo payment", null);
        }
    }

    public String signHmacSHA256(String data, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return toHexString(rawHmac);
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        try (Formatter formatter = new Formatter(sb)) {
            for (byte b : bytes) {
                formatter.format("%02x", b);
            }
        }
        return sb.toString();
    }
}
