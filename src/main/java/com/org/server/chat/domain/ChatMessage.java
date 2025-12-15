package com.org.server.chat.domain;


import com.org.server.chat.domain.ChatType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chatmessage")
@Getter
@NoArgsConstructor
public class ChatMessage {

    @Id
    private String id;
    private Long roomId;
    private Long senderId;
    private String content;
    private boolean deleted=false;
    private String createDate;
    private String updateDate;

    @Builder
    private ChatMessage(Long roomId, Long senderId,
                        String content,String id,String createDate,String updateDate) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.content = content;
        this.id=id;
        this.createDate=createDate;
        this.updateDate=updateDate;
    }

}
