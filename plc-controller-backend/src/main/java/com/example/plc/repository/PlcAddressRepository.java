package com.example.plc.repository;

import com.example.plc.entity.PlcAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlcAddressRepository extends JpaRepository<PlcAddress, Long> {
    List<PlcAddress> findByDeviceId(Long deviceId);
    List<PlcAddress> findByButtonSchemeId(Long buttonSchemeId);
}