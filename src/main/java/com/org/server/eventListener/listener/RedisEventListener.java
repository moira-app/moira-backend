package com.org.server.eventListener.listener;


import com.org.server.eventListener.domain.RedisEvent;
import com.org.server.eventListener.domain.RedisEventEnum;
import com.org.server.redis.service.RedisIntegralService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class RedisEventListener {

    private final RedisIntegralService redisIntegralService;

    @TransactionalEventListener(phase= AFTER_COMMIT)
    public void redisEventHandler(RedisEvent redisEvent){
        switch (redisEvent.getRedisEventEnum()){
            case RedisEventEnum.TICKETCREATE -> {
                String memberId=(String) redisEvent.getData().get("memberId");
                String projectId=(String) redisEvent.getData().get("projectId");
                redisIntegralService.setTicketKey(projectId,memberId);
            }
            case RedisEventEnum.TICKETDEL -> {
                String memberId=(String) redisEvent.getData().get("memberId");
                String projectId=(String) redisEvent.getData().get("projectId");
                redisIntegralService.delTicketKey(projectId,memberId);
            }
            case RedisEventEnum.PROJECTDEL -> {
                String projectId=(String) redisEvent.getData().get("projectId");
                redisIntegralService.delProjectKey(projectId);
            }
            case RedisEventEnum.MEMBERUPDATE -> {
                Long memberId=(Long) redisEvent.getData().get("memberId");
                String member=(String) redisEvent.getData().get("memberData");
                redisIntegralService.setUserInfo(memberId,member);
            }
        }
    }


}


