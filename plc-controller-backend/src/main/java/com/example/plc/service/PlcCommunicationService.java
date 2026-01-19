package com.example.plc.service;

import com.example.plc.client.AbstractPLCClient;
import com.example.plc.entity.PlcAddress;
import com.example.plc.entity.PlcDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;


@Service
public class PlcCommunicationService {

    private final Map<String, Object> currentValues = new HashMap<>();
    
    // PLC设备服务
    private final PlcDeviceService plcDeviceService;
    // PLC连接日志服务
    private final PlcConnectionLogService logService;
    // PLC通信线程池
    private final Executor plcExecutor;
    // 线程安全锁，保护共享资源
    private final ReentrantLock lock = new ReentrantLock();

    public PlcCommunicationService(PlcDeviceService plcDeviceService,
                                   PlcConnectionLogService logService,
                                   @Qualifier("plcExecutor") Executor plcExecutor) {
        this.plcDeviceService = plcDeviceService;
        this.logService = logService;
        this.plcExecutor = plcExecutor;
        logService.addLog("[PLC连接] 初始化PLC通信服务，使用设备管理服务获取设备信息");
    }

    /**
     * 连接指定设备
     * @param deviceId 设备ID
     * @return CompletableFuture 连接结果
     */
    public CompletableFuture<Void> connect(Long deviceId) {
        // 使用线程池异步执行连接操作
        return CompletableFuture.runAsync(() -> {
            try {
                plcDeviceService.connectDevice(deviceId);
            } catch (Exception e) {
                logService.addLog("[PLC连接] 连接设备失败: ID=" + deviceId + ", 错误: " + e.getMessage());
            }
        }, plcExecutor);
    }

    /**
     * 断开指定设备连接
     * @param deviceId 设备ID
     * @return CompletableFuture 断开结果
     */
    public CompletableFuture<Void> disconnect(Long deviceId) {
        // 使用线程池异步执行断开连接操作
        return CompletableFuture.runAsync(() -> {
            plcDeviceService.disconnectDevice(deviceId);
        }, plcExecutor);
    }

    /**
     * 检查指定设备是否已连接
     * @param deviceId 设备ID
     * @return 连接状态
     */
    public boolean isConnected(Long deviceId) {
        return plcDeviceService.isDeviceConnected(deviceId);
    }

    /**
     * 检查是否有任何设备已连接
     * @return 连接状态
     */
    public boolean isConnected() {
        return plcDeviceService.getAllDevices().stream()
                .anyMatch(device -> device.isEnabled() && plcDeviceService.isDeviceConnected(device.getId()));
    }

    public CompletableFuture<Object> readValue(PlcAddress address) {
        // 使用线程池异步执行读取操作
        return CompletableFuture.supplyAsync(() -> {
            Long deviceId = address.getDeviceId();
            if (deviceId == null) {
                logService.addLog("[PLC读取] 地址未关联设备: " + address.getAddress());
                return null;
            }
            
            // 获取设备客户端
            AbstractPLCClient client = plcDeviceService.getDeviceClient(deviceId);
            if (client == null || !client.isConnected()) {
                logService.addLog("[PLC读取] 设备未连接，无法读取数据: ID=" + deviceId + ", 地址: " + address.getAddress());
                return null;
            }
            
            Object value = null;
            int modbusAddress = parseAddress(address.getAddress());
            
            try {
                if (address.getDataType() == PlcAddress.DataType.BOOLEAN) {
                    // 布尔类型
                    if (address.getType() == PlcAddress.AddressType.INPUT) {
                        // 输入线圈 (Discrete Inputs) - 功能码 02
                        value = client.readDiscreteInput(modbusAddress, 1)[0];
                    } else {
                        // 输出线圈 (Coils) - 功能码 01
                        value = client.readCoil(modbusAddress, 1)[0];
                    }
                } else if (address.getDataType() == PlcAddress.DataType.NUMBER) {
                    // 数值类型
                    if (address.getType() == PlcAddress.AddressType.INPUT) {
                        // 输入寄存器 (Input Registers) - 功能码 04
                        value = client.readInputRegister(modbusAddress, 1)[0];
                    } else {
                        // 保持寄存器 (Holding Registers) - 功能码 03
                        value = client.readHoldingRegister(modbusAddress, 1)[0];
                    }
                }
                
                // 保存当前值
                String key = deviceId + ":" + address.getAddress();
                currentValues.put(key, value);
                return value;
            } catch (Exception e) {
                logService.addLog("[PLC读取] 读取失败: ID=" + deviceId + ", 地址: " + address.getAddress() + ", 错误: " + e.getMessage());
                // 读取失败时触发设备离线并重新连接
                try {
                    logService.addLog("[PLC读取] 触发设备离线并重新连接: ID=" + deviceId);
                    // 断开设备连接
                    plcDeviceService.disconnectDevice(deviceId);
                    // // 重新连接设备
                    // plcDeviceService.connectDevice(deviceId);
                } catch (Exception reconnectionException) {
                    // logService.addLog("[PLC读取] 重新连接失败: ID=" + deviceId + ", 错误: " + reconnectionException.getMessage());
                }
                return null;
            }
        }, plcExecutor);
    }

    public CompletableFuture<Boolean> writeValue(PlcAddress address, Object value) {
        // 使用线程池异步执行写入操作
        return CompletableFuture.supplyAsync(() -> {
            Long deviceId = address.getDeviceId();
            if (deviceId == null) {
                logService.addLog("[PLC写入] 地址未关联设备: " + address.getAddress());
                return false;
            }
            
            // 获取设备客户端
            AbstractPLCClient client = plcDeviceService.getDeviceClient(deviceId);
            if (client == null || !client.isConnected()) {
                logService.addLog("[PLC写入] 设备未连接，无法写入数据: ID=" + deviceId + ", 地址: " + address.getAddress());
                return false;
            }
            
            int modbusAddress = parseAddress(address.getAddress());
            
            try {
                if (address.getDataType() == PlcAddress.DataType.BOOLEAN) {
                    // 布尔类型 - 保持寄存器 (Holding Registers) - 功能码 06
                    short shortValue = Short.parseShort(value.toString());
                    client.writeSingleHoldingRegister(modbusAddress, shortValue);
                } else if (address.getDataType() == PlcAddress.DataType.NUMBER) {
                    // 数值类型 - 保持寄存器 (Holding Registers) - 功能码 06
                    short shortValue = Short.parseShort(value.toString());
                    client.writeSingleHoldingRegister(modbusAddress, shortValue);
                }
                
                logService.addLog("[PLC写入] 成功 - ID=" + deviceId + ", 地址: " + address.getAddress() + "，值: " + value);
                // 保存当前值
                String key = deviceId + ":" + address.getAddress();
                currentValues.put(key, value);
                return true;
            } catch (Exception e) {
                logService.addLog("[PLC写入] 写入失败 - ID=" + deviceId + ", 地址: " + address.getAddress() + "，值: " + value + "，错误: " + e.getMessage());
                return false;
            }
        }, plcExecutor);
    }
    
    /**
     * 解析PLC地址为Modbus地址
     * 支持格式：1x (线圈), 2x (离散输入), 3x (保持寄存器), 4x (输入寄存器)
     * @param address PLC地址字符串
     * @return Modbus地址 (直接使用原始地址，不进行偏移转换)
     */
    private int parseAddress(String address) {
        // 移除空格
        address = address.trim();
        
        // 提取地址前缀和数字部分
        String prefix = address.substring(0, 1);
        int numAddress = Integer.parseInt(address.substring(1).replaceAll("[^0-9]", ""));
        
        // Modbus地址转换 (直接使用原始地址，不进行偏移转换)
        switch (prefix.toLowerCase()) {
            case "1": // 线圈 - 0xxxx
                return numAddress;
            case "2": // 离散输入 - 1xxxx
                return numAddress;
            case "3": // 保持寄存器 - 4xxxx
                return numAddress;
            case "4": // 输入寄存器 - 3xxxx
                return numAddress;
            default:
                throw new IllegalArgumentException("不支持的地址类型: " + prefix);
        }
    }
    
    public Map<String, Object> getCurrentValues() {
        lock.lock();
        try {
            // 返回一个新的HashMap，避免外部修改内部状态
            return new HashMap<>(currentValues);
        } finally {
            lock.unlock();
        }
    }

    public Object getCurrentValue(Long deviceId, String address) {
        lock.lock();
        try {
            String key = deviceId + ":" + address;
            return currentValues.get(key);
        } finally {
            lock.unlock();
        }
    }
    
    public Object getCurrentValue(String address) {
        lock.lock();
        try {
            // 兼容旧接口，尝试查找第一个匹配的地址值
            for (Map.Entry<String, Object> entry : currentValues.entrySet()) {
                if (entry.getKey().endsWith(":" + address)) {
                    return entry.getValue();
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }
}