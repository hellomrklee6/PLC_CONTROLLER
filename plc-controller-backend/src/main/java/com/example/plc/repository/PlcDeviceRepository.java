package com.example.plc.repository;

import com.example.plc.entity.PlcDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * PLC设备仓库接口，用于设备数据的数据库操作
 */
@Repository
public interface PlcDeviceRepository extends JpaRepository<PlcDevice, Long> {
}