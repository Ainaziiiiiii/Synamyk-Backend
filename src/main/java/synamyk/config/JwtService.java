package synamyk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateToken(UserDetails userDetails, String phone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phone", phone);
        claims.put("username", userDetails.getUsername());
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return generateToken(claims);
    }

    public String generateToken(Map<String, Object> claims) {
        try {
            long nowMillis = System.currentTimeMillis();
            long expMillis = nowMillis + jwtExpirationMs;

            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new HashMap<>(claims);
            payload.put("iat", nowMillis / 1000);
            payload.put("exp", expMillis / 1000);

            String encodedHeader = base64UrlEncode(objectMapper.writeValueAsString(header));
            String encodedPayload = base64UrlEncode(objectMapper.writeValueAsString(payload));

            String signatureInput = encodedHeader + "." + encodedPayload;
            String signature = createSignature(signatureInput);

            return signatureInput + "." + signature;
        } catch (Exception e) {
            log.error("Error generating token: {}", e.getMessage());
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String signatureInput = parts[0] + "." + parts[1];
            String expectedSignature = createSignature(signatureInput);

            if (!expectedSignature.equals(parts[2])) {
                log.error("Invalid signature");
                return false;
            }

            Map<String, Object> payload = decodePayload(parts[1]);
            Long exp = ((Number) payload.get("exp")).longValue();

            if (Instant.now().getEpochSecond() > exp) {
                log.error("Token expired");
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        try {
            Map<String, Object> payload = extractPayload(token);
            return (String) payload.get("username");
        } catch (Exception e) {
            log.error("Error extracting username: {}", e.getMessage());
            return null;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username != null && username.equals(userDetails.getUsername()) && validateToken(token);
    }

    public Map<String, Object> extractPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            return decodePayload(parts[1]);
        } catch (Exception e) {
            log.error("Error extracting payload: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private String createSignature(String data) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKeySpec);
            byte[] hash = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create signature", e);
        }
    }

    private String base64UrlEncode(String data) {
        return base64UrlEncode(data.getBytes(StandardCharsets.UTF_8));
    }

    private String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private String base64UrlDecode(String encoded) {
        byte[] decoded = Base64.getUrlDecoder().decode(encoded);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    private Map<String, Object> decodePayload(String encodedPayload) {
        try {
            String json = base64UrlDecode(encodedPayload);
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            log.error("Error decoding payload: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
}