package com.example.plc.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class PlcWebSocketController {

    /**
     * 接收客户端消息并广播
     */
    @MessageMapping("/plc/update")
    @SendTo("/topic/plc/data")
    public String sendPlcData(String message) {
        return message;
    }
}
