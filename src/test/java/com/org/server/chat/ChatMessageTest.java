package com.org.server.chat;

import com.org.server.chat.domain.ChatMessage;
import com.org.server.chat.domain.ChatMessageDto;
import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.repository.ChatMessageAdvanceRepository;
import com.org.server.member.domain.Member;
import com.org.server.project.domain.Project;
import com.org.server.support.IntegralTestEnv;
import com.org.server.ticket.domain.Master;
import com.org.server.ticket.domain.Ticket;
import com.org.server.util.DateTimeMapUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class ChatMessageTest extends IntegralTestEnv {



    Member member;

    Project project;


    ChatRoom chatRoom;


    @BeforeEach
    void setting(){
        member=createMember(0L);
        project=createProject("Test","Test");
        chatRoom=createChatRoom(project);
    }
    @Test
    @DisplayName("채팅 삭제 테스트")
    void testChatDel(){

        ChatMessageDto chatMessageDto=chatMessageService.sendMessage(chatRoom.getId()
                ,member.getId(),"test",DateTimeMapUtil.provietTimeToString(LocalDateTime.now()));
        chatMessageService.delMsg(chatMessageDto.id(),chatMessageDto.senderId());
        ChatMessage chatMessage=chatMessageRepository.findById(chatMessageDto.id()).get();

        assertThat(chatMessage.getDeleted()).isTrue();

    }

    @Test
    @DisplayName("채팅 발행 테스트")
    void testChatCreate(){
        chatMessageService.sendMessage(chatRoom.getId(),member.getId(),"test",DateTimeMapUtil.provietTimeToString(LocalDateTime.now()));
        List<ChatMessage> chatMessageList=chatMessageRepository.findAll();
        assertThat(chatMessageList.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("채팅 내용 업데이트 테스트")
    void testUpdateChat(){
        LocalDateTime now=LocalDateTime.now();
        LocalDateTime updateDate=LocalDateTime.now().plusSeconds(60);
        ChatMessageDto chatMessageDto=chatMessageService.sendMessage(chatRoom.getId(),member.getId(),"test","2025-12-12 12:12:12");
        chatMessageService.updateMsg(chatMessageDto.id(),"updatetest",chatMessageDto.senderId(),"2025-12-12 12:12:55");
        List<ChatMessageDto>chatMessageDtos=chatMessageService.getMsgList(null,chatRoom.getId(),null);
        assertThat(chatMessageDtos.getFirst().content()).isEqualTo("updatetest");
        assertThat(chatMessageDtos.getFirst().updateDate()).isEqualTo("2025.12.12.12.12");
    }

    @Test
    @DisplayName("커서 기반 페이징 및 채팅 초기입장시 호출 테스트")
    void testCallChats(){

        String reMemberTime="";
        List<ChatMessageDto> chatMessageDtos=new ArrayList<>();
        for(int i=0;20>i;i++){

            LocalDateTime now=LocalDateTime.now();
            if(i==10){
                reMemberTime=DateTimeMapUtil.provietTimeToString(now);
            }
            ChatMessageDto data=chatMessageService.sendMessage(chatRoom.getId(), member.getId(), "test",DateTimeMapUtil.provietTimeToString(now));
            chatMessageDtos.add(data);
        }
        List<ChatMessage> chatMessageDtos2= chatMessageAdvanceRepository.findMessages(
                null,chatRoom.getId(),null);
        assertThat(chatMessageDtos2.size()).isEqualTo(10);
        List<ChatMessage> chatMessageDtos3=chatMessageAdvanceRepository.findMessages(chatMessageDtos2.getLast().getId(),chatRoom.getId()
                ,reMemberTime);
        assertThat(chatMessageDtos3.size()).isEqualTo(10);
        assertThat(chatMessageDtos3.getFirst().getId()).isEqualTo(chatMessageDtos.get(9).id());
    }
}
