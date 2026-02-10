package com.example.plc.repository;

import com.example.plc.entity.PlcAddressChangeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PlcAddressChangeLogRepository extends JpaRepository<PlcAddressChangeLog, Long> {

    List<PlcAddressChangeLog> findAllByOrderByChangeTimeDesc();
    Page<PlcAddressChangeLog> findAllByOrderByChangeTimeDesc(Pageable pageable);
    
    List<PlcAddressChangeLog> findByAddressIdOrderByChangeTimeDesc(Long addressId);
    Page<PlcAddressChangeLog> findByAddressIdOrderByChangeTimeDesc(Long addressId, Pageable pageable);

    @Query("SELECT l FROM PlcAddressChangeLog l WHERE l.changeTime BETWEEN :startTime AND :endTime ORDER BY l.changeTime DESC")
    List<PlcAddressChangeLog> findByChangeTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT l FROM PlcAddressChangeLog l WHERE l.changeTime BETWEEN :startTime AND :endTime ORDER BY l.changeTime DESC")
    Page<PlcAddressChangeLog> findByChangeTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, Pageable pageable);

    @Query("SELECT l FROM PlcAddressChangeLog l WHERE l.addressId = :addressId AND l.changeTime BETWEEN :startTime AND :endTime ORDER BY l.changeTime DESC")
    List<PlcAddressChangeLog> findByAddressIdAndChangeTimeBetween(
            @Param("addressId") Long addressId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT l FROM PlcAddressChangeLog l WHERE l.addressId = :addressId AND l.changeTime BETWEEN :startTime AND :endTime ORDER BY l.changeTime DESC")
    Page<PlcAddressChangeLog> findByAddressIdAndChangeTimeBetween(
            @Param("addressId") Long addressId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );
}

