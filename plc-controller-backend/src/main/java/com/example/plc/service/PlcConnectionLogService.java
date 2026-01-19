package com.example.plc.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * PLC连接日志服务，用于收集和推送连接相关的日志
 */
@Service
public class PlcConnectionLogService {
    
    // 日志队列，用于存储连接相关的日志
    private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    // 日志历史记录，最多保留200条
    private final List<String> logHistory = new ArrayList<>();
    // 最大日志历史记录数量
    private static final int MAX_LOG_HISTORY = 200;
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public PlcConnectionLogService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        // 启动日志推送线程
        startLogPushThread();
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * 添加连接日志
     * @param log 日志内容
     */
    public void addLog(String log) {
        log= sdf.format(new java.util.Date()) + " :" + log;
        System.out.println(log);
        try {
            // 不再过滤日志，保留所有连接相关日志
            logQueue.offer(log, 1, TimeUnit.SECONDS);
            
            // 同步更新历史记录
            synchronized (logHistory) {
                logHistory.add(log);
                // 保持历史记录不超过MAX_LOG_HISTORY
                if (logHistory.size() > MAX_LOG_HISTORY) {
                    logHistory.remove(0);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 获取日志历史记录
     * @return 日志历史记录列表
     */
    public List<String> getLogHistory() {
        synchronized (logHistory) {
            return new ArrayList<>(logHistory);
        }
    }
    
    /**
     * 启动日志推送线程
     */
    private void startLogPushThread() {
        Thread pushThread = new Thread(() -> {
            while (true) {
                try {
                    // 从队列中获取日志
                    String log = logQueue.poll(500, TimeUnit.MILLISECONDS);
                    if (log != null) {
                        // 通过WebSocket推送日志到前端
                        messagingTemplate.convertAndSend("/topic/plc/connection-log", log);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    
                }
            }
        });
        
        pushThread.setDaemon(true);
        pushThread.start();
    }
}