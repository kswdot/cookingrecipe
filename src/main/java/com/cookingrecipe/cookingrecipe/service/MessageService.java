package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.domain.Message;
import com.cookingrecipe.cookingrecipe.domain.User.User;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.repository.MessageRepository;
import com.cookingrecipe.cookingrecipe.service.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;

    // 메시지 저장
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    // 읽지 않은 메시지 개수 조회
    public int getUnreadMessageCount(Long receiverId) {
        return messageRepository.countUnreadMessages(receiverId);
    }

    // 보낸 메시지 조회
    public List<Message> getSentMessages(Long senderId) {
        return messageRepository.findSentMessages(senderId);
    }

    // 받은 메시지 조회
    public List<Message> getReceivedMessages(Long receiverId) {
        return messageRepository.findReceivedMessages(receiverId);
    }

    // 메세지 단일 조회
    public Message findMessageById(Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalStateException("해당 메시지를 찾을 수 없습니다"));
    }

    // 메세지 상태 읽음으로 변경
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalStateException("해당 메시지를 찾을 수 없습니다: " + messageId));

        if (!message.isReadStatus()) {
            message.setReadStatus(true); // 읽음 상태로 변경
            messageRepository.save(message); // 변경된 메시지 저장
        }
    }

    public User findUserByLoginId(String loginId) {
        return userService.findByLoginId(loginId)
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));
    }
}
