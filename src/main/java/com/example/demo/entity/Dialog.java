package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dialog", uniqueConstraints = @UniqueConstraint(columnNames = {"first_person_id", "second_person_id"}))
public class Dialog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_person_id")
    private Long firstPersonId;
    @Column(name = "second_person_id")
    private Long secondPersonId;
    @Column(name = "last_active_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastActiveTime;
    @Column(name = "last_message")
    private String lastMessage;

}
