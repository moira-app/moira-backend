package com.org.server.scheduler.scheduledTask;


import com.org.server.scheduler.repository.SchedulerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Transactional
public class ProjectDelScheduler implements ScheduledFuture{

    private final SchedulerRepository schedulerRepository;

    @Override
    public String taskName() {
        return "projectDelTask";
    }

    //@Async
    @Override
    public CompletableFuture<String> scheduledTask() {
        try {
            schedulerRepository.delTicketQuery();
            return CompletableFuture.completedFuture(taskName());
        }
        catch (Exception e){
            return CompletableFuture.failedFuture(new RuntimeException());
        }
    }
}
