package com.cookingrecipe.cookingrecipe.dto;

import com.cookingrecipe.cookingrecipe.domain.Message;
import com.cookingrecipe.cookingrecipe.domain.User.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private Long messageId; // 쪽지 번호

    @JsonProperty("sender")
    private String senderId; // 쪽지 발송자

    @JsonProperty("receiver")
    private String receiverId; // 쪽지 수신자

    @NotBlank(message = "내용을 입력해 주세요")
    private String content; // 내용

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime; // 송신 시간

    private boolean readStatus; // 읽음 여부

    public Message dtoToEntity(User sender, User receiver){
        return new Message(messageId, sender, receiver, content, sendTime, readStatus);
    }

    public static MessageDto entityToDto(Message messageEntity){
        System.out.println("Entity sendTime: " + messageEntity.getSendTime()); // 값 확인
        return new MessageDto(
                messageEntity.getId(),
                messageEntity.getSender().getLoginId(),
                messageEntity.getReceiver().getLoginId(),
                messageEntity.getContent(),
                messageEntity.getSendTime(),
                messageEntity.isReadStatus()
        );
    }
}
