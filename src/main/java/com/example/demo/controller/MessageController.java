package com.example.demo.controller;

import com.example.demo.dto.DialogDTO;
import com.example.demo.entity.Message;
import com.example.demo.service.ImageService;
import com.example.demo.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dialog")
public class MessageController {
    private final MessageService messageService;
    private final ImageService imageService;

    @GetMapping("/all")
    public ResponseEntity<List<DialogDTO>> getAllDialogs(Principal principal) {
        List<DialogDTO> allDialogForUser = messageService.getAllDialogForUser(principal);
        List<DialogDTO> dialogDTOS = imageService.setImageByDialogs(allDialogForUser,principal);
        return new ResponseEntity<>(dialogDTOS, HttpStatus.OK);
    }
    @PutMapping("/read")
    public ResponseEntity<?> updateReadStatus(@RequestBody List<Long> messageIds) {
        messageService.updateReadStatus(messageIds);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{userId}/create")
    public ResponseEntity<Object> createMessageByID(@RequestBody String message,
                                                    Principal principal,
                                                    @PathVariable String userId) {
        Message message1 = messageService.createMessageById(message, principal, Long.parseLong(userId));
        return new ResponseEntity<>(message1, HttpStatus.OK);
    }
//    @GetMapping("/all/messages")
//    public ResponseEntity<Message> getAllMessage(Principal principal,) {
//    }


    @GetMapping("{id}")
    public ResponseEntity<List<Message>> getMessageFromIdDialog(@PathVariable String id) {
        List<Message> messagesByIdDialog = messageService.getMessagesByIdDialog(Long.parseLong(id));
        return ResponseEntity.ok(messagesByIdDialog);
    }
}
