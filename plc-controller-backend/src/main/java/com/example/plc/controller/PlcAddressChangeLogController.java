package com.example.plc.controller;

import com.example.plc.entity.PlcAddressChangeLog;
import com.example.plc.service.PlcAddressChangeLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/changelogs")
public class PlcAddressChangeLogController {

    private final PlcAddressChangeLogService service;

    public PlcAddressChangeLogController(PlcAddressChangeLogService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<PlcAddressChangeLog>> getAllChangeLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PlcAddressChangeLog> logs = service.findAll(pageable);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/address/{addressId}")
    public ResponseEntity<Page<PlcAddressChangeLog>> getChangeLogsByAddressId(
            @PathVariable Long addressId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PlcAddressChangeLog> logs = service.findByAddressId(addressId, pageable);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/time-range")
    public ResponseEntity<Page<PlcAddressChangeLog>> getChangeLogsByTimeRange(
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PlcAddressChangeLog> logs = service.findByChangeTimeBetween(startTime, endTime, pageable);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/address/{addressId}/time-range")
    public ResponseEntity<Page<PlcAddressChangeLog>> getChangeLogsByAddressIdAndTimeRange(
            @PathVariable Long addressId,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PlcAddressChangeLog> logs = service.findByAddressIdAndChangeTimeBetween(addressId, startTime, endTime, pageable);
        return ResponseEntity.ok(logs);
    }
}
