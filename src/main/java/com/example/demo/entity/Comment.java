package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Post post;
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private Long userId;
    @Column(columnDefinition = "text",nullable = false)
    private String message;
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @PrePersist
    protected void onCreate(){
        this.createdDate = LocalDateTime.now();
    }

}
