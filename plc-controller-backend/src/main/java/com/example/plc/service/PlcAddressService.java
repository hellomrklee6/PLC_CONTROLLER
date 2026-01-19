package com.example.plc.service;

import com.example.plc.entity.PlcAddress;
import com.example.plc.repository.PlcAddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlcAddressService {

    private final PlcAddressRepository repository;

    public PlcAddressService(PlcAddressRepository repository) {
        this.repository = repository;
    }

    public List<PlcAddress> findAll() {
        List<PlcAddress> addresses = repository.findAll();

        return addresses;
    }

    public Optional<PlcAddress> findById(Long id) {
        return repository.findById(id);
    }
    
    public List<PlcAddress> findByDeviceId(Long deviceId) {
        return repository.findByDeviceId(deviceId);
    }

    public PlcAddress save(PlcAddress address) {
        return repository.save(address);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
    /**
     * 批量更新地址的设备ID
     * @param addressIds 地址ID列表
     * @param deviceId 新的设备ID
     */
    public void batchUpdateDeviceId(List<Long> addressIds, Long deviceId) {
        List<PlcAddress> addresses = repository.findAllById(addressIds);
        for (PlcAddress address : addresses) {
            address.setDeviceId(deviceId);
            repository.save(address);
        }
    }
}