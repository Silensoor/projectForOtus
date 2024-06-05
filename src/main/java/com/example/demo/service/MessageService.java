package com.example.demo.service;

import com.example.demo.dto.DialogDTO;
import com.example.demo.entity.Dialog;
import com.example.demo.entity.Message;
import com.example.demo.entity.User;
import com.example.demo.facade.DialogFacade;
import com.example.demo.repository.DialogRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserRepository userRepository;
    private final DialogRepository dialogRepository;
    private final DialogFacade dialogFacade;
    private final MessageRepository messageRepository;

    public Message createMessage(String message, Principal principal, String username) {
        User user = getUserByPrincipal(principal);
        Dialog dialog;
        User userSecond = userRepository.findUserByEmail(username)
                .orElseGet(() -> userRepository.findUserByUserName(username)
                        .orElseThrow(() -> new UsernameNotFoundException
                                ("User not found with username or email: " + username)));

        return getMessage(message, user, userSecond);
    }

    public Message createMessageById(String message, Principal principal, Long userId) {
        User user = getUserByPrincipal(principal);
        Dialog dialog;
        User userSecond = userRepository.findUserById(userId)
                .orElseGet(() -> userRepository.findUserById(userId)
                        .orElseThrow(() -> new UsernameNotFoundException
                                ("User not found with username or email: " + userId)));

        return getMessage(message, user, userSecond);
    }

    private Message getMessage(String message, User user, User userSecond) {
        Dialog dialog;
        dialog = dialogRepository.findDialogByFirstPersonIdAndSecondPersonId(user.getId(), userSecond.getId())
                .orElseGet(() -> dialogRepository.findDialogByFirstPersonIdAndSecondPersonId(userSecond.getId(), user.getId())
                        .orElse(null));

        if (dialog == null) {
            dialog = createDialog(message, user, userSecond);
        }

        dialog.setLastMessage(message);
        dialog.setLastActiveTime(new Date());
        dialogRepository.saveAndFlush(dialog);

        Message responseMessage = createMessage(message, user, userSecond, dialog);
        messageRepository.saveAndFlush(responseMessage);
        return responseMessage;
    }

    private Dialog createDialog(String message, User user, User secondUser) {
        Dialog dialog = new Dialog();
        dialog.setFirstPersonId(user.getId());
        dialog.setSecondPersonId(secondUser.getId());
        return dialog;
    }

    private Message createMessage(String message, User user, User secondUser, Dialog dialog) {
        Message message1 = new Message();
        message1.setMessageText(message);
        message1.setCreateTime(new Date());
        message1.setDialog(dialog);
        message1.setAuthorId(user.getId());
        message1.setIsDeleted(false);
        message1.setReadStatus(false);
        return message1;
    }

    public List<DialogDTO> getAllDialogForUser(Principal principal) {
        User user = getUserByPrincipal(principal);
        List<Dialog> dialogByFirstPersonIdOrSecondPersonId = dialogRepository.findDialogByFirstPersonIdOrSecondPersonId(user.getId(), user.getId());
        List<DialogDTO> list = dialogByFirstPersonIdOrSecondPersonId.stream().map(dialogFacade::dialogToDialogDTO).toList();
        return list.stream().peek(dialog -> {
            Optional<User> byId = userRepository.findById(dialog.getSecondPersonId());
            if (user.getId().equals(dialog.getFirstPersonId())) {
               dialog.setFirstPersonName(user.getName()==null?user.getUsersName():user.getName());
                dialog.setSecondPersonName(byId.get().getName()==null?byId.get().getUsersName():byId.get().getName());
            } else {
                dialog.setSecondPersonName(user.getName()==null?user.getUsersName():user.getName());
                dialog.setFirstPersonName(byId.get().getName()==null?byId.get().getUsersName():byId.get().getName());
            }
        }).toList();
    }

    public List<Message> getMessagesByIdDialog(Long id) {
        Optional<Dialog> dialog = dialogRepository.findById(id);
        if (dialog.isPresent()) {
            return messageRepository.findMessageByDialog(dialog.get());
        } else {
            return new ArrayList<>();
        }

    }

    private User getUserByPrincipal(Principal principal) {
        String userName = principal.getName();
        return userRepository.findUserByEmail(userName).
                orElseThrow(() -> new UsernameNotFoundException("UserName not found " + userName));
    }

    public void updateReadStatus(List<Long> messageIds) {
        List<Message> messages = messageRepository.findAllById(messageIds);
        for (Message message : messages) {
            message.setReadStatus(true);
        }
        messageRepository.saveAll(messages);
    }
}
