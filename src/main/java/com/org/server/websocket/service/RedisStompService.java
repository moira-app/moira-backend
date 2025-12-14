package com.org.server.websocket.service;


import com.org.server.redis.service.LuaScriptSet;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisStompService {
    private static final String stompSessionBaseKey="BASEKEY";
    private static final String stompSessionKey="stomp-session-";
    private final RedisTemplate<String,String> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    public boolean checkSessionKeyExist(String memberId){
        Long result=stringRedisTemplate.execute(new DefaultRedisScript<>(LuaScriptSet.checkStompSessionExistScript,Long.class)
                , List.of(stompSessionKey+memberId),stompSessionBaseKey);
        if(result==1){
            return false;
        }
        return true;
    }
    public List<String> getSubScribeAndDelKey(String memberId){
        List<String> result=stringRedisTemplate.execute(new DefaultRedisScript<>(LuaScriptSet.getSubScribeListAndDelScript,List.class)
                ,List.of(stompSessionKey+memberId));
        return result;
    }
    public void addSubScribeDest(String memberId,String dest){
        redisTemplate.opsForSet().add(stompSessionKey+memberId,dest);
    }
    public void removeSubScribeDest(String memberId,String dest){
        redisTemplate.opsForSet().remove(stompSessionKey+memberId,dest);
    }
    public void delIntegralSubDest(String memberId){
        redisTemplate.opsForSet().remove(stompSessionKey+memberId);
    }
}
