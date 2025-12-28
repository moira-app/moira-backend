package com.org.server.graph;

import com.org.server.graph.dto.NodeDelDto;
import com.org.server.graph.dto.NodeDto;
import com.org.server.graph.dto.StructureChangeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class GraphAop {


    private final RedissonClient redissonClient;
    private final static String delStructureKey="del-structure-";


    @Around("@annotation(com.org.server.graph.GraphTransaction)")
    public Object mongoRedissonLock(ProceedingJoinPoint point) throws Throwable{
        NodeDto nodeDto =(NodeDto) point.getArgs()[0];
        RLock rLock;
        switch (nodeDto.getGraphActionType()){
            case GraphActionType.Delete ->{
                NodeDelDto nodeDelDto=(NodeDelDto) nodeDto;
                rLock=redissonClient.getLock(delStructureKey+nodeDelDto.getRootId());
            }
            default -> {
                StructureChangeDto structureChangeDto=(StructureChangeDto) nodeDto;
                rLock = redissonClient.getLock(delStructureKey +structureChangeDto.getRootId());
            }
        }
        try{
            boolean rockState=rLock.tryLock(10000L,10000L, TimeUnit.MILLISECONDS);
            if(!rockState){
                throw new RuntimeException("대시시간 초과 발생, 재시도 해주세요");
            }
            log.info("redssion 트랜잭션 작동 시작");
            return point.proceed();
        }
        catch (Exception e){
            log.info("트리구조 수정중 에러발생:{}",e.getMessage());
            throw  e;
            //소켓 통신 에러 통합 컨트롤링 방법 생각하기.
        }
        finally {
            log.info("redssion lock 반납");
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
}

