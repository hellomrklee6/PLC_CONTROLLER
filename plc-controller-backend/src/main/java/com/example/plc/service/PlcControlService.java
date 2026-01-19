package com.example.plc.service;

import com.example.plc.entity.PlcAddress;
import com.example.plc.repository.PlcAddressRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class PlcControlService {

    private final PlcAddressRepository addressRepository;
    private final PlcCommunicationService communicationService;
    private final PlcDataCollectionService dataCollectionService;

    public PlcControlService(PlcAddressRepository addressRepository,
                            PlcCommunicationService communicationService,
                            PlcDataCollectionService dataCollectionService) {
        this.addressRepository = addressRepository;
        this.communicationService = communicationService;
        this.dataCollectionService = dataCollectionService;
    }

    public CompletableFuture<Boolean> controlPlcAddress(Long addressId, Object value) {
        Optional<PlcAddress> addressOptional = addressRepository.findById(addressId);
        if (addressOptional.isPresent()) {
            PlcAddress address = addressOptional.get();
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