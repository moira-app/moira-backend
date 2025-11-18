package com.org.server.graph;

import com.org.server.exception.MoiraException;
import com.org.server.exception.MoiraSocketException;
import com.org.server.graph.dto.NodeDelDto;
import com.org.server.graph.dto.NodeDto;
import com.org.server.graph.dto.PropertyChangeDto;
import com.org.server.graph.dto.StructureChangeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class GraphAop {


    private final RedissonClient redissonClient;
    private final static String nodeStructureChange="structure-";


    @Around("@annotation(com.org.server.graph.GraphTransaction)")
    public Object mongoRedissonLock(ProceedingJoinPoint point) throws Throwable{

        MethodSignature methodSignature=(MethodSignature) point.getSignature();

        NodeDto nodeDto =(NodeDto) point.getArgs()[0];
        RLock rLock;
        StructureChangeDto structureChangeDto=(StructureChangeDto) nodeDto;

        String nodeId=structureChangeDto.getNodeId();
        rLock = redissonClient.getLock(nodeStructureChange +nodeId);

        try{
            boolean rockState=rLock.tryLock(2000L,2000L, TimeUnit.MILLISECONDS);
            if(!rockState){
                throw new RuntimeException("대시시간 초과 발생, 재시도 해주세요");
            }
            log.info("redssion 트랜잭션 작동 시작");
            return point.proceed();
        }
        catch (MoiraSocketException e){
            log.info("error in here");
            throw new MoiraSocketException(e.getMessage(),e.getProjectId(),e.getRequestId(),e.getRootId());
        }
        catch (Exception e){
            log.info("error in here2:{}",e.getMessage());
            throw new MoiraSocketException(e.getMessage(),nodeDto.getProjectId(),nodeDto.getRequestId(),nodeDto.getRootId());
        }
        finally {
            log.info("redssion lock 반납");
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
}

