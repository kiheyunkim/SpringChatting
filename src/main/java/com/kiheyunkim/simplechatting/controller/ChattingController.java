package com.kiheyunkim.simplechatting.controller;

import com.kiheyunkim.simplechatting.config.WebSocketHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class ChattingController {
    private WebSocketHandler webSocketHandler;

    public ChattingController(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @GetMapping("/send")
    @ResponseBody
    public void getSend(String message) {
        System.out.println(message);
        try {
            webSocketHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
