package com.couplespace.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "anniversaries")
public class Anniversary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "anniversary_date", nullable = false)
    private LocalDate anniversaryDate;

    @Column(name = "anniversary_type", nullable = false)
    private String anniversaryType; // "TOGETHER", "BIRTHDAY_MALE", "BIRTHDAY_FEMALE", "CUSTOM"

    @Column(name = "is_recurring", nullable = false)
    private Boolean isRecurring = false; // 是否每年重复

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 构造函数
    public Anniversary() {}

    public Anniversary(String title, String description, LocalDate anniversaryDate,
                       String anniversaryType, Boolean isRecurring, Long createdBy) {
        this.title = title;
        this.description = description;
        this.anniversaryDate = anniversaryDate;
        this.anniversaryType = anniversaryType;
        this.isRecurring = isRecurring;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getter和Setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getAnniversaryDate() { return anniversaryDate; }
    public void setAnniversaryDate(LocalDate anniversaryDate) { this.anniversaryDate = anniversaryDate; }

    public String getAnniversaryType() { return anniversaryType; }
    public void setAnniversaryType(String anniversaryType) { this.anniversaryType = anniversaryType; }

    public Boolean getIsRecurring() { return isRecurring; }
    public void setIsRecurring(Boolean isRecurring) { this.isRecurring = isRecurring; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}