package com.org.server.scheduler.scheduledTask;

import org.hibernate.type.descriptor.java.JdbcTimeJavaType;

import java.util.concurrent.CompletableFuture;

public interface ScheduledFuture {
    public String taskName();
    public CompletableFuture<String> scheduledTask();
}
