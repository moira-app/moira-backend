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
    public void removeSubScribeDest(String memberId){
        redisTemplate.opsForSet().remove(stompSessionKey+memberId);
    }
}
