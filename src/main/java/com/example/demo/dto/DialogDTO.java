package com.example.demo.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

import java.util.Date;

@Data
public class DialogDTO {
    private Long id;

    private Long firstPersonId;

    private Long secondPersonId;


    private Date lastActiveTime;

    private String lastMessage;
    private byte[] image;
    private String secondPersonName;
    private String firstPersonName;
}
