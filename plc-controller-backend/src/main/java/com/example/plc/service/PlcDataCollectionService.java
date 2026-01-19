package com.example.plc.service;

import com.example.plc.entity.PlcAddress;
import com.example.plc.entity.PlcDevice;
import com.example.plc.repository.PlcAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PlcDataCollectionService {

    private final PlcAddressRepository addressRepository;
    private final PlcCommunicationService communicationService;
    private final PlcDeviceService plcDeviceService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<Long, Object> plcStatusMap = new ConcurrentHashMap<>();

    @Autowired
    public PlcDataCollectionService(PlcAddressRepository addressRepository,
                                    PlcCommunicationService communicationService,
                                    PlcDeviceService plcDeviceService,
                                    SimpMessagingTemplate messagingTemplate) {
        this.addressRepository = addressRepository;
        this.communicationService = communicationService;
        this.plcDeviceService = plcDeviceService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRateString = "${plc.refresh-interval}")
    public void collectData() {
        // 获取所有地址
        List<PlcAddress> addresses = addressRepository.findAll();
        
        // 按设备ID分组
        Map<Long, List<PlcAddress>> addressesByDevice = addresses.stream()
                .filter(address -> address.getDeviceId() != null)
                .collect(Collectors.groupingBy(PlcAddress::getDeviceId));
        
        if (addressesByDevice.isEmpty()) {
            // 没有关联到设备的地址，不进行数据采集
            return;
        }
        
        Map<Long, PlcData> plcDataMap = new HashMap<>();
        List<CompletableFuture<?>> futures = new ArrayList<>();
        
        // 为每个设备读取数据
        for (Map.Entry<Long, List<PlcAddress>> entry : addressesByDevice.entrySet()) {
            Long deviceId = entry.getKey();
            List<PlcAddress> deviceAddresses = entry.getValue();
            
            // 检查设备是否已连接
            if (!communicationService.isConnected(deviceId)) {
                continue;
            }
            
            // 为设备的每个地址创建异步读取任务
            for (PlcAddress address : deviceAddresses) {
                CompletableFuture<?> future = communicationService.readValue(address)
                    .thenAccept(value -> {
                        // ConcurrentHashMap不允许null值，所以需要处理null情况
                        if (value != null) {
                            plcStatusMap.put(address.getId(), value);
                        } else {
                            // 如果value为null，可以选择保留之前的值或者使用默认值
                            // 这里选择保留之前的值
                            if (!plcStatusMap.containsKey(address.getId())) {
                                // 如果是第一次读取且为null，可以设置一个默认值
                                if (address.getDataType() == PlcAddress.DataType.BOOLEAN) {
                                    plcStatusMap.put(address.getId(), false);
                                    value = false;
                                } else {
                                    plcStatusMap.put(address.getId(), 0);
                                    value = 0;
                                }
                            } else {
                                // 使用之前的值
                                value = plcStatusMap.get(address.getId());
                            }
                        }
                        
                        // 创建PLC数据对象
                        PlcData plcData = new PlcData();
                        plcData.setId(address.getId());
                        plcData.setName(address.getName());
                        plcData.setAddress(address.getAddress());
                        plcData.setType(address.getType().name());
                        plcData.setDataType(address.getDataType().name());
                        plcData.setDescription(address.getDescription());
                        plcData.setDeviceId(deviceId);
                        plcData.setValue(value);
                        
                        plcDataMap.put(address.getId(), plcData);
                    });
                
                futures.add(future);
            }
        }
        
        // 等待所有异步读取操作完成
        if (!futures.isEmpty()) {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            // 通过WebSocket广播数据到客户端
            messagingTemplate.convertAndSend("/topic/plc/data", plcDataMap);
        }
    }

    public Map<Long, Object> getPlcStatusMap() {
        return plcStatusMap;
    }

    public Object getStatusByAddressId(Long addressId) {
        return plcStatusMap.get(addressId);
    }

    public void refreshData() {
        collectData();
    }
    
    // PLC数据传输对象
    public static class PlcData {
        private Long id;
        private String name;
        private String address;
        private String type;
        private String dataType;
        private String description;
        private Long deviceId;
        private Object value;
        
        // getter and setter methods
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getAddress() {
            return address;
        }
        
        public void setAddress(String address) {
            this.address = address;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getDataType() {
            return dataType;
        }
        
        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Long getDeviceId() {
            return deviceId;
        }
        
        public void setDeviceId(Long deviceId) {
            this.deviceId = deviceId;
        }
        
        public Object getValue() {
            return value;
        }
        
        public void setValue(Object value) {
            this.value = value;
        }
    }
}