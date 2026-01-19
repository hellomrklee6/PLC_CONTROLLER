package com.example.plc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置类
 * 将PLC通信操作放到单独的线程池中执行，避免阻塞主线程
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * PLC通信线程池
     * 增加核心线程数和最大线程数，确保能同时处理多个PLC操作
     */
    @Bean("plcExecutor")
    public Executor plcExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(5);
        // 最大线程数
        executor.setMaxPoolSize(10);
        // 队列容量
        executor.setQueueCapacity(200);
        // 线程名称前缀
        executor.setThreadNamePrefix("PLC-Thread-");
        // 线程活跃时间
        executor.setKeepAliveSeconds(60);
        // 拒绝策略：CallerRunsPolicy表示如果线程池已满，由调用者线程执行任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }
}
