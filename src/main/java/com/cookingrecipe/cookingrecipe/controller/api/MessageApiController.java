package com.cookingrecipe.cookingrecipe.controller.api;

import com.cookingrecipe.cookingrecipe.domain.Message;
import com.cookingrecipe.cookingrecipe.domain.User.CustomUserDetails;
import com.cookingrecipe.cookingrecipe.domain.User.User;
import com.cookingrecipe.cookingrecipe.dto.MessageDto;
import com.cookingrecipe.cookingrecipe.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageApiController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    // 쪽지 전송
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody MessageDto messageDto) {
        // 로그인된 사용자 가져오기
        User sender = getCurrentUser();

        // 수신자 가져오기 (loginId 기반)
        User receiver = messageService.findUserByLoginId(messageDto.getReceiverId());

        // DTO → Entity 변환
        Message message = messageDto.dtoToEntity(sender, receiver);

        // 메시지 저장
        messageService.saveMessage(message);

        // 실시간 알림 전송
        int unreadCount = messageService.getUnreadMessageCount(receiver.getId());
        messagingTemplate.convertAndSendToUser(
                receiver.getId().toString(), // 수신자의 WebSocket 경로
                "/queue/messages",
                "새로운 쪽지 " + unreadCount + "개"
        );

        return ResponseEntity.ok("쪽지 전송 완료");
    }


    // 읽지 않은 쪽지 개수 조회
    @GetMapping("/unread-count")
    public int getUnreadMessageCount() {
        // 로그인된 사용자 가져오기
        User sender = getCurrentUser();

        return messageService.getUnreadMessageCount(sender.getId());
    }


    // 보낸 쪽지 조회
    @GetMapping("/sent")
    public List<MessageDto> getSentMessages() {
        // 로그인된 사용자 가져오기
        User sender = getCurrentUser();

        return messageService.getSentMessages(sender.getId())
                .stream()
                .map(MessageDto::entityToDto)
                .collect(Collectors.toList());
    }


    // 받은 쪽지 조회
    @GetMapping("/received")
    public List<MessageDto> getReceivedMessages() {
        // 로그인된 사용자 가져오기
        User sender = getCurrentUser();

        return messageService.getReceivedMessages(sender.getId())
                .stream()
                .map(MessageDto::entityToDto)
                .collect(Collectors.toList());
    }

    // 단일 메세지 조회
    @GetMapping("/{messageId}")
    public ResponseEntity<MessageDto> getMessageById(@PathVariable Long messageId) {
        Message message = messageService.findMessageById(messageId);
        return ResponseEntity.ok(MessageDto.entityToDto(message));
    }

    // 메세지 읽음으로 변경
    @PatchMapping("/{messageId}/read")
    public ResponseEntity<String> markMessageAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return ResponseEntity.ok("메시지를 읽음 상태로 변경했습니다.");
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }
}
