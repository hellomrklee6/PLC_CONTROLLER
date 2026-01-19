package com.example.plc.controller;

import com.example.plc.entity.PlcAddress;
import com.example.plc.service.PlcAddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/addresses")
public class PlcAddressController {

    private final PlcAddressService service;

    public PlcAddressController(PlcAddressService service) {
        this.service = service;
    }

    @GetMapping
    public List<PlcAddress> getAllAddresses() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlcAddress> getAddressById(@PathVariable Long id) {
        Optional<PlcAddress> address = service.findById(id);
        return address.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlcAddress> createAddress(@RequestBody PlcAddress address) {
        PlcAddress savedAddress = service.save(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlcAddress> updateAddress(@PathVariable Long id, @RequestBody PlcAddress address) {
        Optional<PlcAddress> existingAddress = service.findById(id);
        if (existingAddress.isPresent()) {
            address.setId(id);
            PlcAddress updatedAddress = service.save(address);
            return ResponseEntity.ok(updatedAddress);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        Optional<PlcAddress> address = service.findById(id);
        if (address.isPresent()) {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 批量更新地址的设备ID
     * @param request 包含地址ID列表和新设备ID的请求体
     * @return 响应状态
     */
    @PutMapping("/batch/device")
    public ResponseEntity<Void> batchUpdateDeviceId(@RequestBody BatchDeviceUpdateRequest request) {
        service.batchUpdateDeviceId(request.getAddressIds(), request.getDeviceId());
        return ResponseEntity.noContent().build();
    }
    
    // 批量更新请求体
    public static class BatchDeviceUpdateRequest {
        private List<Long> addressIds;
        private Long deviceId;
        
        // getter and setter methods
        public List<Long> getAddressIds() {
            return addressIds;
        }
        
        public void setAddressIds(List<Long> addressIds) {
            this.addressIds = addressIds;
        }
        
        public Long getDeviceId() {
            return deviceId;
        }
        
        public void setDeviceId(Long deviceId) {
            this.deviceId = deviceId;
        }
    }
}