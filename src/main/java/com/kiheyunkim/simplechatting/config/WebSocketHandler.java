package com.kiheyunkim.simplechatting.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private List<WebSocketSession> sessionList = new ArrayList<>();
    private Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    private WebSocketSession mySession;

    static private int counter = 0;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionList.add(session);
        this.mySession = session;
        logger.info("Connected : " + String.valueOf(counter));

        Map<String, Object> attributes = session.getAttributes();
        attributes.put("counter", counter);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("Recv : " + session.getAttributes().get("counter") + " : " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Disonnected : " + session.getAttributes().get("counter"));
        sessionList.remove(session);
    }

    public void sendMessage(String message) throws IOException {
        if(mySession!=null){
            mySession.sendMessage(new TextMessage(message));
        }
    }
}
