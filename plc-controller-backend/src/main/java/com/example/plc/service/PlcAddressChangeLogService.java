package com.example.plc.service;

import com.example.plc.entity.PlcAddress;
import com.example.plc.entity.PlcAddressChangeLog;
import com.example.plc.repository.PlcAddressChangeLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlcAddressChangeLogService {

    private final PlcAddressChangeLogRepository repository;

    public PlcAddressChangeLogService(PlcAddressChangeLogRepository repository) {
        this.repository = repository;
    }

    /**
     * 记录PLC地址值的变更
     * @param address PLC地址实体
     * @param oldValue 旧值
     * @param newValue 新值
     */
    public void recordChange(PlcAddress address, String oldValue, String newValue) {
        System.out.println("[DEBUG] recordChange called: addressId=" + address.getId() + ", isStoreInDb=" + address.isStoreInDb() + ", oldValue=" + oldValue + ", newValue=" + newValue);
        if (!address.isStoreInDb()) {
            System.out.println("[DEBUG] Address not configured to store in db, skipping record");
            return; // 如果配置为不入库，则不记录
        }

        PlcAddressChangeLog log = new PlcAddressChangeLog();
        log.setAddressId(address.getId());
        log.setAddressName(address.getName());
        log.setAddress(address.getAddress());
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setChangeTime(LocalDateTime.now());

//        System.out.println("[DEBUG] Saving change log: " + log);
        PlcAddressChangeLog savedLog = repository.save(log);
//        System.out.println("[DEBUG] Saved change log: " + savedLog);
    }

    /**
     * 根据地址ID查询变更记录
     * @param addressId 地址ID
     * @return 变更记录列表
     */
    public List<PlcAddressChangeLog> findByAddressId(Long addressId) {
        return repository.findByAddressIdOrderByChangeTimeDesc(addressId);
    }

    /**
     * 根据时间范围查询变更记录
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 变更记录列表
     */
    public List<PlcAddressChangeLog> findByChangeTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return repository.findByChangeTimeBetween(startTime, endTime);
    }

    /**
     * 根据地址ID和时间范围查询变更记录
     * @param addressId 地址ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 变更记录列表
     */
    public List<PlcAddressChangeLog> findByAddressIdAndChangeTimeBetween(Long addressId, LocalDateTime startTime, LocalDateTime endTime) {
        return repository.findByAddressIdAndChangeTimeBetween(addressId, startTime, endTime);
    }

    /**
     * 查询所有变更记录
     * @return 变更记录列表
     */
    public List<PlcAddressChangeLog> findAll() {
        return repository.findAllByOrderByChangeTimeDesc();
    }

    /**
     * 分页查询所有变更记录
     * @param pageable 分页参数
     * @return 分页变更记录
     */
    public Page<PlcAddressChangeLog> findAll(Pageable pageable) {
        return repository.findAllByOrderByChangeTimeDesc(pageable);
    }

    /**
     * 分页查询指定地址的变更记录
     * @param addressId 地址ID
     * @param pageable 分页参数
     * @return 分页变更记录
     */
    public Page<PlcAddressChangeLog> findByAddressId(Long addressId, Pageable pageable) {
        return repository.findByAddressIdOrderByChangeTimeDesc(addressId, pageable);
    }

    /**
     * 分页查询指定时间范围的变更记录
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 分页变更记录
     */
    public Page<PlcAddressChangeLog> findByChangeTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return repository.findByChangeTimeBetween(startTime, endTime, pageable);
    }

    /**
     * 分页查询指定地址和时间范围的变更记录
     * @param addressId 地址ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 分页变更记录
     */
    public Page<PlcAddressChangeLog> findByAddressIdAndChangeTimeBetween(Long addressId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return repository.findByAddressIdAndChangeTimeBetween(addressId, startTime, endTime, pageable);
    }
}
