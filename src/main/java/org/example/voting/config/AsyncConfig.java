package org.example.voting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "voteTaskExecutor")
    public Executor voteTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Base number of threads keeping the engine warm
        executor.setCorePoolSize(50);

        // Maximum threads to spin up during peak broadcast voting windows
        executor.setMaxPoolSize(200);

        // The memory queue to hold votes when all 200 threads are busy.
        // Sized to comfortably hold a few seconds of peak 5,000/sec traffic.
        executor.setQueueCapacity(15000);

        // Thread name prefix for easier debugging in the immutable audit logs
        executor.setThreadNamePrefix("VoteWorker-");

        // CRITICAL FOR ZERO VOTE LOSS:
        // If the queue hits 15,000, do not drop the vote. Force the caller (the HTTP thread)
        // to execute it. This temporarily slows down response times but guarantees no valid votes are lost.
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // Ensure worker threads shut down gracefully, finishing their queues before server shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }
}