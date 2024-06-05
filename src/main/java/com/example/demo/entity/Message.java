package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "message_text", columnDefinition = "TEXT")
    private String messageText;
    @Column(name = "read_status")
    private Boolean readStatus;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createTime;
    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Dialog dialog;
    @Column(name = "author_id")
    private Long authorId;

}
