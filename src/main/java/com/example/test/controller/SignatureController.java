package com.example.test.controller;

import com.example.test.config.AppConfig;
import com.example.test.exception.ForbiddenException;
import com.example.test.model.Signature;
import com.example.test.model.SignatureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequiredArgsConstructor
public class SignatureController {

    private final AppConfig appConfig;

    @PostMapping("/signature/{operationId}")
    public SignatureResponse getSignature(
            @RequestHeader(value = "Token", required = false) String token,
            @RequestParam Map<String, String> params,
            @PathVariable String operationId
    ) {
        if (!appConfig.getToken().equals(token)) {
            throw new ForbiddenException();
        }

        return new SignatureResponse()
                .setStatus("success")
                .setResult(List.of(new Signature(generateSignature(params))));
    }

    private String generateSignature(Map<String, String> params) {
        TreeMap<String, String> sortedParams = new TreeMap<>(params);
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        if (!stringBuilder.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1); // удалить последний '&'
        }

        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(appConfig.getToken().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] signatureBytes = sha256Hmac.doFinal(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
            return bytesToHex(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
