package com.example.plc.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "button_scheme")
public class ButtonScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    // 移除直接的OneToMany关系，避免表创建顺序问题
    // 按钮将通过ButtonRepository单独管理

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
