package com.example.plc.listener;

import com.example.plc.service.PlcConnectionLogService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;

/**
 * WebSocket事件监听器，用于处理客户端订阅事件
 * 在客户端订阅日志主题时，发送历史日志记录
 */
@Component
public class WebSocketEventListener {

    private final PlcConnectionLogService logService;
    private final SimpMessageSendingOperations messagingTemplate;

    public WebSocketEventListener(PlcConnectionLogService logService, SimpMessageSendingOperations messagingTemplate) {
        this.logService = logService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 监听客户端订阅事件
     * 当客户端订阅PLC连接日志主题时，发送历史日志记录
     * @param event 订阅事件
     */
    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();

        // 检查客户端是否订阅了PLC连接日志主题
        if (destination != null && destination.equals("/topic/plc/connection-log")) {
            // 获取日志历史记录
            List<String> logHistory = logService.getLogHistory();
            
            // 逐个发送历史日志到客户端
            for (String log : logHistory) {
                // 使用convertAndSend发送到公共主题
                // 因为客户端已经订阅了这个主题，所以会接收到消息
                messagingTemplate.convertAndSend("/topic/plc/connection-log", log);
            }
        }
    }
}