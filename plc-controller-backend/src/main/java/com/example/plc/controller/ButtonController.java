package com.example.plc.controller;

import com.example.plc.entity.Button;
import com.example.plc.repository.ButtonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/buttons")
public class ButtonController {

    private final ButtonRepository repository;

    public ButtonController(ButtonRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/scheme/{schemeId}")
    public List<Button> getButtonsBySchemeId(@PathVariable Long schemeId) {
        return repository.findBySchemeId(schemeId);
    }

    @PostMapping
    public ResponseEntity<Button> createButton(@RequestBody Button button) {
        Button savedButton = repository.save(button);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedButton);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Button> updateButton(@PathVariable Long id, @RequestBody Button button) {
        Optional<Button> existingButton = repository.findById(id);
        if (existingButton.isPresent()) {
            button.setId(id);
            Button updatedButton = repository.save(button);
            return ResponseEntity.ok(updatedButton);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteButton(@PathVariable Long id) {
        Optional<Button> button = repository.findById(id);
        if (button.isPresent()) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/scheme/{schemeId}")
    public ResponseEntity<Void> deleteButtonsBySchemeId(@PathVariable Long schemeId) {
        List<Button> buttons = repository.findBySchemeId(schemeId);
        for (Button button : buttons) {
            repository.deleteById(button.getId());
        }
        return ResponseEntity.noContent().build();
    }
}
