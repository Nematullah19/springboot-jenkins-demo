package com.example.jenkinsdemo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/hsm")
public class HsmController {

    private final HsmService hsmService;

    public HsmController(HsmService hsmService) {
        this.hsmService = hsmService;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "connected",
                hsmService.isConnected());

        response.put(
                "provider",
                hsmService.getProviderName());

        response.put(
                "keyAlias",
                hsmService.getKeyAlias());

        return response;
    }

    @PostMapping("/sign")
    public ResponseEntity<?> sign(
            @RequestBody SignRequest request) {

        try {

            String signature =
                    hsmService.sign(request.message());

            return ResponseEntity.ok(
                    Map.of(
                            "message", request.message(),
                            "signature", signature));

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(
            @RequestBody VerifyRequest request) {

        try {

            boolean valid =
                    hsmService.verify(
                            request.message(),
                            request.signature());

            return ResponseEntity.ok(
                    Map.of("valid", valid));

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()));
        }
    }

    public record SignRequest(String message) {}

    public record VerifyRequest(
            String message,
            String signature) {}
}
