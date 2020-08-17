package com.kiheyunkim.simplechatting.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@EnableWebSocket
@ServerEndpoint(value = "/socket")
public class Socket{
    private Session session;
    private static Set<Socket> listeners = new CopyOnWriteArraySet<>();
    private Logger logger = LoggerFactory.getLogger(Socket.class);

    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        listeners.add(this);
        logger.info("onOpen called");
    }

    @OnClose
    public void onClose(Session session){
        listeners.remove(this);
        logger.info("onClose called");
    }

    @OnMessage
    public void onMessage(String message){
        logger.info(message);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("onMessage called");
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        listeners.remove(this);
        logger.info("onError called");
    }
}

/*
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
*/
