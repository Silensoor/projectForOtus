package com.example.demo.repository;

import com.example.demo.entity.Dialog;
import com.example.demo.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findMessageByDialog(Dialog dialog);
}
