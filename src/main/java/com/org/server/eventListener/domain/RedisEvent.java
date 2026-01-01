package com.org.server.eventListener.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class RedisEvent {
    private RedisEventEnum redisEventEnum;
    private Map<String,Object> data;
    @Builder
    public RedisEvent(RedisEventEnum redisEventEnum, Map<String,Object> data) {
        this.redisEventEnum = redisEventEnum;
        this.data = data;
    }
}
