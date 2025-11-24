package com.org.server.scheduler;



import com.org.server.scheduler.scheduledTask.MongoDelScheduler;
import com.org.server.scheduler.scheduledTask.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.endpoints.internal.Literal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class SchedulingWork {


    private final List<ScheduledFuture> scheduledFutures;

    //@Scheduled(cron = "0 0 4 * * *")
    public void scheduledWorkProcess(){

        CompletableFuture<Void> [] futures=scheduledFutures.stream()
                .map(x->{
                   CompletableFuture<String> f= x.scheduledTask();
                    return f
                            .thenApply(y->{
                                log.info("정상 종료-{}-{}",y,LocalDateTime.now());
                                return null;
                            })
                            .exceptionally(y-> {
                                String[] arr = y.getMessage().split("-");
                                log.info("err 발생-{}-{}-{}", arr[1], arr[0], LocalDateTime.now());
                                //메시지 큐에 남기는 역할.
                                return null;
                            });

                })
                .toArray(CompletableFuture[]::new);
        CompletableFuture<Void> allOfFuture=CompletableFuture.allOf(futures);
        allOfFuture.thenRun(()->{
            log.info("스케쥴러 절차 종료");
        });

    }
}
