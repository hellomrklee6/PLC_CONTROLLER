package com.example.plc.controller;

import com.example.plc.service.PlcControlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/control")
public class PlcControlController {

    private final PlcControlService controlService;

    public PlcControlController(PlcControlService controlService) {
        this.controlService = controlService;
    }

    @PostMapping("/{addressId}")
    public CompletableFuture<ResponseEntity<Boolean>> controlAddress(@PathVariable Long addressId, @RequestBody ControlRequest request) {
        return controlService.controlPlcAddress(addressId, request.getValue())
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<Object> getCurrentValue(@PathVariable Long addressId) {
        Object value = controlService.getCurrentValue(addressId);
        return ResponseEntity.ok(value);
    }

    public static class ControlRequest {
        private Object value;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}