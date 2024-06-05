package com.example.demo.facade;

import com.example.demo.dto.DialogDTO;
import com.example.demo.entity.Dialog;
import org.springframework.stereotype.Component;

@Component
public class DialogFacade {
    public DialogDTO dialogToDialogDTO(Dialog dialog) {
        DialogDTO dto = new DialogDTO();
        dto.setId(dialog.getId());
        dto.setFirstPersonId(dialog.getFirstPersonId());
        dto.setLastActiveTime(dialog.getLastActiveTime());
        dto.setLastMessage(dialog.getLastMessage());
        dto.setSecondPersonId(dialog.getSecondPersonId());

        return dto;
    }

}
