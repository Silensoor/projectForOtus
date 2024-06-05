package com.example.demo.repository;

import com.example.demo.entity.Dialog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DialogRepository extends JpaRepository<Dialog, Long> {
    List<Dialog> findDialogByFirstPersonIdOrSecondPersonId(Long firstPersonId, Long secondPersonId);
    Optional<Dialog> findDialogByFirstPersonIdAndSecondPersonId(Long firstPersonId, Long secondPersonId);
}
