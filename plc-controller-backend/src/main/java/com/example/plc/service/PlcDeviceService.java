package com.example.plc.service;

import com.example.plc.client.AbstractPLCClient;
import com.example.plc.client.ModbusTCPClient;
import com.example.plc.client.OpcUaClient;
import com.example.plc.entity.PlcAddress;
import com.example.plc.entity.PlcDevice;
import com.example.plc.repository.PlcAddressRepository;
import com.example.plc.repository.PlcDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * PLC设备管理服务，用于管理多个PLC设备
 */
@Service
public class PlcDeviceService {

    private final PlcDeviceRepository deviceRepository;
    private final PlcAddressRepository addressRepository;
    private final PlcConnectionLogService logService;
    private final Executor plcExecutor;
    private final SimpMessageSendingOperations messagingTemplate;

    // 设备客户端映射，键为设备ID
    private final Map<Long, AbstractPLCClient> deviceClients = new HashMap<>();

    @Autowired
    public PlcDeviceService(PlcDeviceRepository deviceRepository,
                           PlcAddressRepository addressRepository,
                           PlcConnectionLogService logService,
                           @org.springframework.beans.factory.annotation.Qualifier("plcExecutor") Executor plcExecutor,
                           SimpMessageSendingOperations messagingTemplate) {
        this.deviceRepository = deviceRepository;
        this.addressRepository = addressRepository;
        this.logService = logService;
        this.plcExecutor = plcExecutor;
        this.messagingTemplate = messagingTemplate;
        // 初始化设备客户端
        initializeDeviceClients();
    }

    /**
     * 初始化设备客户端
     */
    private void initializeDeviceClients() {
        List<PlcDevice> devices = deviceRepository.findAll();
        for (PlcDevice device : devices) {
            if (device.isEnabled()) {
                createDeviceClient(device);
            }
        }
    }

    /**
     * 创建设备客户端
     * @param device 设备信息
     */
    private void createDeviceClient(PlcDevice device) {
        AbstractPLCClient client = null;
        switch (device.getProtocol()) {
            case "ModbusTCP":
                client = new ModbusTCPClient(device.getIp(), device.getPort());
                break;
            case "OPCUA":
                client = new OpcUaClient(device.getIp(), device.getPort());
                break;
            // 可添加其他协议的客户端创建逻辑
            default:
                logService.addLog("[设备管理] 不支持的协议: " + device.getProtocol());
                return;
        }

        deviceClients.put(device.getId(), client);
        logService.addLog("[设备管理] 创建设备客户端: " + device.getName() + " (" + device.getProtocol() + ")");
    }

    /**
     * 获取所有设备
     * @return 设备列表
     */
    public List<PlcDevice> getAllDevices() {
        return deviceRepository.findAll();
    }

    /**
     * 根据ID获取设备
     * @param id 设备ID
     * @return 设备信息
     */
    public PlcDevice getDeviceById(Long id) {
        return deviceRepository.findById(id).orElse(null);
    }

    /**
     * 创建设备
     * @param device 设备信息
     * @return 创建的设备
     */
    public PlcDevice createDevice(PlcDevice device) {
        PlcDevice savedDevice = deviceRepository.save(device);
        if (device.isEnabled()) {
            createDeviceClient(savedDevice);
        }
        logService.addLog("[设备管理] 创建设备: " + device.getName());
        return savedDevice;
    }

    /**
     * 更新设备
     * @param id 设备ID
     * @param device 更新后的设备信息
     * @return 更新后的设备
     */
    public PlcDevice updateDevice(Long id, PlcDevice device) {
        PlcDevice existingDevice = deviceRepository.findById(id).orElse(null);
        if (existingDevice == null) {
            return null;
        }

        // 更新设备信息
        existingDevice.setName(device.getName());
        existingDevice.setIp(device.getIp());
        existingDevice.setPort(device.getPort());
        existingDevice.setProtocol(device.getProtocol());
        existingDevice.setDescription(device.getDescription());
        existingDevice.setEnabled(device.isEnabled());

        PlcDevice updatedDevice = deviceRepository.save(existingDevice);

        // 更新设备客户端
        if (device.isEnabled()) {
            // 先移除旧客户端
            deviceClients.remove(id);
            // 创建新客户端
            createDeviceClient(updatedDevice);
        } else {
            // 禁用设备，移除客户端
            AbstractPLCClient client = deviceClients.remove(id);
            if (client != null) {
                try {
                    client.disconnect();
                } catch (Exception e) {
                    logService.addLog("[设备管理] 断开设备连接失败: " + e.getMessage());
                }
            }
        }

        logService.addLog("[设备管理] 更新设备: " + device.getName());
        return updatedDevice;
    }

    /**
     * 删除设备
     * @param id 设备ID
     * @return 是否删除成功
     */
    public boolean deleteDevice(Long id) {
        // 检查是否有地址关联到该设备
        List<PlcAddress> addresses = addressRepository.findByDeviceId(id);
        if (!addresses.isEmpty()) {
            return false;
        }

        // 移除设备客户端
        AbstractPLCClient client = deviceClients.remove(id);
        if (client != null) {
            try {
                client.disconnect();
            } catch (Exception e) {
                logService.addLog("[设备管理] 断开设备连接失败: " + e.getMessage());
            }
        }

        // 删除设备
        deviceRepository.deleteById(id);
        logService.addLog("[设备管理] 删除设备: ID=" + id);
        return true;
    }

    /**
     * 连接设备
     * @param deviceId 设备ID
     * @throws Exception 连接异常
     */
    public void connectDevice(Long deviceId) throws Exception {
        AbstractPLCClient client = deviceClients.get(deviceId);
        if (client == null) {
            PlcDevice device = deviceRepository.findById(deviceId).orElse(null);
            if (device == null) {
                throw new Exception("设备不存在");
            }
            createDeviceClient(device);
            client = deviceClients.get(deviceId);
        }

        if (client != null) {
            client.connect();
            logService.addLog("[设备管理] 连接设备成功: ID=" + deviceId);
            // 广播设备状态变化
            broadcastDeviceStatus(deviceId);
        }
    }

    /**
     * 断开设备连接
     * @param deviceId 设备ID
     */
    public void disconnectDevice(Long deviceId) {
        AbstractPLCClient client = deviceClients.get(deviceId);
        if (client != null) {
            try {
                client.disconnect();
                logService.addLog("[设备管理] 断开设备连接成功: ID=" + deviceId);
                // 广播设备状态变化
                broadcastDeviceStatus(deviceId);
            } catch (Exception e) {
                logService.addLog("[设备管理] 断开设备连接失败: " + e.getMessage());
                // 即使断开失败，也广播状态变化
                broadcastDeviceStatus(deviceId);
            }
        }
    }

    /**
     * 获取设备客户端
     * @param deviceId 设备ID
     * @return 设备客户端实例
     */
    public AbstractPLCClient getDeviceClient(Long deviceId) {
        return deviceClients.get(deviceId);
    }
    
    /**
     * 广播设备状态变化到前端
     * @param deviceId 设备ID
     */
    private void broadcastDeviceStatus(Long deviceId) {
        boolean connected = isDeviceConnected(deviceId);
        Map<String, Object> status = new HashMap<>();
        status.put("deviceId", deviceId);
        status.put("connected", connected);
        status.put("timestamp", System.currentTimeMillis());
        
        // 发送到WebSocket主题
        messagingTemplate.convertAndSend("/topic/plc/device-status", status);
    }
    
    /**
     * 检查设备连接状态
     * @param deviceId 设备ID
     * @return 连接状态
     */
    public boolean isDeviceConnected(Long deviceId) {
        AbstractPLCClient client = deviceClients.get(deviceId);
        return client != null && client.isConnected();
    }
    
    /**
     * 更新设备启用状态
     * @param deviceId 设备ID
     * @param enabled 启用状态
     */
    public void updateDeviceEnabled(Long deviceId, boolean enabled) {
        // 获取设备
        PlcDevice device = deviceRepository.findById(deviceId).orElseThrow(() -> 
            new RuntimeException("设备不存在"));
        
        // 更新设备启用状态
        device.setEnabled(enabled);
        deviceRepository.save(device);
        
        // 根据启用状态创建或移除设备客户端
        if (enabled) {
            // 如果设备被启用，创建客户端并连接
            createDeviceClient(device);
            logService.addLog("[设备管理] 启用设备: " + device.getName());
        } else {
            // 如果设备被禁用，移除客户端
            AbstractPLCClient client = deviceClients.remove(deviceId);
            if (client != null) {
                try {
                    client.disconnect();
                } catch (Exception e) {
                    logService.addLog("[设备管理] 断开设备连接失败: " + e.getMessage());
                }
            }
            logService.addLog("[设备管理] 禁用设备: " + device.getName());
        }
    }
Long n = 0L;
    /**
     * 定时检查设备连接状态
     */
    @Scheduled(fixedRate = 2000) // 每2秒检查一次
    public void checkDeviceConnections() {
        List<PlcDevice> devices = deviceRepository.findAll();
        for (PlcDevice device : devices) {
            if (device.isEnabled()) {
                boolean connected = isDeviceConnected(device.getId());
                if (!connected) {
                    // 尝试重新连接
                    try {
                        connectDevice(device.getId());
                        n = 0L; // 重置尝试次数
                    } catch (Exception e) {
                        // 第n次尝试重新连接
                        n++;
                        logService.addLog("[设备管理] 第" + (n) + "次尝试重新连接设备失败: " + e.getMessage());
                    }
                }
            }
        }
    }
}