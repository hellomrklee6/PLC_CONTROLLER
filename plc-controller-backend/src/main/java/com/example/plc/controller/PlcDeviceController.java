package com.example.plc.controller;

import com.example.plc.entity.PlcDevice;
import com.example.plc.service.PlcDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PLC设备管理控制器，处理设备管理的HTTP请求
 */
@RestController
@RequestMapping("/api/devices")
public class PlcDeviceController {

    private final PlcDeviceService deviceService;

    @Autowired
    public PlcDeviceController(PlcDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * 获取所有设备
     * @return 设备列表
     */
    @GetMapping
    public List<PlcDevice> getAllDevices() {
        return deviceService.getAllDevices();
    }

    /**
     * 根据ID获取设备
     * @param id 设备ID
     * @return 设备信息
     */
    @GetMapping("/{id}")
    public PlcDevice getDeviceById(@PathVariable Long id) {
        return deviceService.getDeviceById(id);
    }

    /**
     * 创建设备
     * @param device 设备信息
     * @return 创建的设备
     */
    @PostMapping
    public PlcDevice createDevice(@RequestBody PlcDevice device) {
        return deviceService.createDevice(device);
    }

    /**
     * 更新设备
     * @param id 设备ID
     * @param device 更新后的设备信息
     * @return 更新后的设备
     */
    @PutMapping("/{id}")
    public PlcDevice updateDevice(@PathVariable Long id, @RequestBody PlcDevice device) {
        return deviceService.updateDevice(id, device);
    }

    /**
     * 删除设备
     * @param id 设备ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable Long id) {
        boolean deleted = deviceService.deleteDevice(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().body("设备已被地址关联，无法删除");
        }
    }

    /**
     * 连接设备
     * @param id 设备ID
     */
    @PostMapping("/{id}/connect")
    public void connectDevice(@PathVariable Long id) {
        try {
            deviceService.connectDevice(id);
        } catch (Exception e) {
            throw new RuntimeException("连接设备失败: " + e.getMessage());
        }
    }

    /**
     * 断开设备连接
     * @param id 设备ID
     */
    @PostMapping("/{id}/disconnect")
    public void disconnectDevice(@PathVariable Long id) {
        deviceService.disconnectDevice(id);
    }

    /**
     * 获取设备连接状态
     * @param id 设备ID
     * @return 连接状态
     */
    @GetMapping("/{id}/status")
    public boolean getDeviceStatus(@PathVariable Long id) {
        return deviceService.isDeviceConnected(id);
    }
    
    /**
     * 更新设备启用状态
     * @param id 设备ID
     * @param enabled 启用状态
     */
    @PutMapping("/{id}/enabled")
    public void updateDeviceEnabled(@PathVariable Long id, @RequestBody boolean enabled) {
        deviceService.updateDeviceEnabled(id, enabled);
    }
}