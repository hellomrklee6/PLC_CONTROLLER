package com.example.plc.controller;

import com.example.plc.entity.ButtonScheme;
import com.example.plc.service.ButtonSchemeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/button-schemes")
public class ButtonSchemeController {

    private final ButtonSchemeService service;

    public ButtonSchemeController(ButtonSchemeService service) {
        this.service = service;
    }

    @GetMapping
    public List<ButtonScheme> getAllSchemes() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ButtonScheme> getSchemeById(@PathVariable Long id) {
        Optional<ButtonScheme> scheme = service.findById(id);
        return scheme.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ButtonScheme> createScheme(@RequestBody ButtonScheme scheme) {
        ButtonScheme savedScheme = service.save(scheme);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedScheme);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ButtonScheme> updateScheme(@PathVariable Long id, @RequestBody ButtonScheme scheme) {
        Optional<ButtonScheme> existingScheme = service.findById(id);
        if (existingScheme.isPresent()) {
            scheme.setId(id);
            ButtonScheme updatedScheme = service.save(scheme);
            return ResponseEntity.ok(updatedScheme);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteScheme(@PathVariable Long id) {
        Optional<ButtonScheme> scheme = service.findById(id);
        if (scheme.isPresent()) {
            boolean deleted = service.deleteById(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().body("按钮方案已被地址引用，无法删除");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
