package com.example.plc.service;

import com.example.plc.entity.PlcAddress;
import com.example.plc.repository.PlcAddressRepository;
import com.example.plc.service.PlcAddressChangeLogService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class PlcControlService {

    private final PlcAddressRepository addressRepository;
    private final PlcCommunicationService communicationService;
    private final PlcDataCollectionService dataCollectionService;
    private final PlcAddressChangeLogService changeLogService;

    public PlcControlService(PlcAddressRepository addressRepository,
                            PlcCommunicationService communicationService,
                            PlcDataCollectionService dataCollectionService,
                            PlcAddressChangeLogService changeLogService) {
        this.addressRepository = addressRepository;
        this.communicationService = communicationService;
        this.dataCollectionService = dataCollectionService;
        this.changeLogService = changeLogService;
    }

    public CompletableFuture<Boolean> controlPlcAddress(Long addressId, Object value) {
        Optional<PlcAddress> addressOptional = addressRepository.findById(addressId);
        if (addressOptional.isPresent()) {
            PlcAddress address = addressOptional.get();
            // 获取旧值
            Object oldValue = getCurrentValue(addressId);
            String oldValueStr = oldValue != null ? oldValue.toString() : "null";
            String newValueStr = value != null ? value.toString() : "null";
            
            // 无论设备是否连接，只要地址配置为入库，就记录变更
            changeLogService.recordChange(address, oldValueStr, newValueStr);
            
            return communicationService.writeValue(address, value)
                    .thenApply(result -> {
                        if (result) {
                            // 立即刷新数据，确保WebSocket客户端能实时看到更新
                            dataCollectionService.refreshData();
                        }
                        return result;
                    });
        }
        return CompletableFuture.completedFuture(false);
    }

    public Object getCurrentValue(Long addressId) {
        Optional<PlcAddress> addressOptional = addressRepository.findById(addressId);
        if (addressOptional.isPresent()) {
            PlcAddress address = addressOptional.get();
            if (address.getDeviceId() != null) {
                // 使用设备ID和地址获取值，确保获取到正确设备的数据
                return communicationService.getCurrentValue(address.getDeviceId(), address.getAddress());
            } else {
                // 兼容旧的单设备模式
                return communicationService.getCurrentValue(address.getAddress());
            }
        }
        return null;
    }
}