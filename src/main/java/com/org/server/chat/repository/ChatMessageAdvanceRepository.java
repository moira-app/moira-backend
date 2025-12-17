package com.org.server.chat.repository;


import com.mongodb.client.result.UpdateResult;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.domain.ChatMessage;
import com.org.server.util.DateTimeMapUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
public class ChatMessageAdvanceRepository {
    private final MongoTemplate mongoTemplate;
    private final ChatMessageRepository messageRepository;


    public ChatMessage createMessage(Long roomId, Long senderId, String content,String createDate){
        LocalDateTime now=LocalDateTime.parse(createDate, DateTimeMapUtil.FLEXIBLE_NANO_FORMATTER);
        ChatMessage chatMessageDoc= ChatMessage.builder()
                .roomId(roomId)
                .senderId(senderId)
                .content(content)
                .createDate(now)
                .updateDate(null)
                .build();
        chatMessageDoc=messageRepository.save(chatMessageDoc);
        return chatMessageDoc;
    }
    public List<ChatMessage> findMessages(String id, Long roomId,String createDate){


        //커서 오프셋 방식으로 찾기.
        if(id!=null&&createDate!=null) {
            LocalDateTime date=LocalDateTime.parse(createDate,DateTimeMapUtil.FLEXIBLE_NANO_FORMATTER);
            Criteria commonCriteria=Criteria.where("roomId").is(roomId)
                    .and("deleted").is(false);
            Criteria finalCondition=new Criteria().orOperator(Criteria.where("createDate").lt(date)
                    ,Criteria.where("createDate").is(date)
                            .and("_id").lt(id));
            commonCriteria.andOperator(finalCondition);
            Query query = new Query(commonCriteria);
            query.limit(10);
            query.with(Sort.by(Sort.Direction.DESC, "createDate")
                    .and(Sort.by(Sort.Direction.DESC,"_id")));
            List<ChatMessage> data = mongoTemplate.find(query, ChatMessage.class);
            return data;
        }
        //latest 메시지
        else{
            Query query = new Query(where("roomId").is(roomId)
                    .and("deleted").is(false));
            query.limit(10);
            query.with(Sort.by(Sort.Direction.DESC, "createDate"));
            List<ChatMessage> data = mongoTemplate.find(query, ChatMessage.class);
            return data;
        }
    }
    //메시지 삭제
    public boolean delMessage(String id,Long senderId){
        Query query = new Query(where("_id").is(id)
                .and("deleted").is(false)
                .and("senderId").is(senderId));
        Update updateData = new Update().set("deleted",true);
        UpdateResult result = mongoTemplate.updateFirst(query, updateData,ChatMessage.class);
        if(result.getModifiedCount()>0){
            return true;
        }
        return false;
    }

    //내용업데이트
    public boolean updateMessage(String id,String content,Long senderId,String updateDate){

        LocalDateTime now=LocalDateTime.parse(updateDate,DateTimeMapUtil.FLEXIBLE_NANO_FORMATTER);
        Query query = new Query(where("_id").is(id)
                .and("deleted").is(false)
                .and("senderId").is(senderId));
        Update update = new Update();
        update.set("content",content);
        update.set("updateDate",now);
        UpdateResult result = mongoTemplate.updateFirst(query,update,ChatMessage.class);
        if(result.getModifiedCount()>0){
            return true;
        }
        return false;
    }



}
