package com.kiheyunkim.simplechatting.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@EnableWebSocket
@ServerEndpoint(value = "/socket")
public class Socket {
    private Session session;
    private String nickname = "fucker";
    private static final Set<Socket> listeners = new CopyOnWriteArraySet<>();
    private final Logger logger = LoggerFactory.getLogger(Socket.class);

    public String getNickname() {
        return nickname;
    }

    private void sendToMe(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    private void sendToOthers(String type, String message) throws IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type",type);
        jsonObject.addProperty("message", message);

        listeners.forEach(listener -> {
            if(listener != this){
                try {
                    listener.session.getBasicRemote().sendText(jsonObject.toString());
                } catch (IOException e) {
                    logger.warn("id: " + session.getId() + "error occurred");
                }
            }
        });
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        listeners.add(this);

        JsonObject returnObject = new JsonObject();
        returnObject.addProperty("nick", getNickname());

        try {
            sendToOthers("join", returnObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        JsonObject returnObject = new JsonObject();
        returnObject.addProperty("type", "joinedList");
        returnObject.addProperty("joinedList", returnObject.toString());

        try {
            sendToMe(returnObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        listeners.remove(this);
    }

    @OnMessage
    public void onMessage(String message) {
        logger.info("msg: " + message);
        JsonElement jsonElement = JsonParser.parseString(message);
        JsonObject parsedMessage = jsonElement.getAsJsonObject();

        String messageType = parsedMessage.get("request").getAsString();
        if (messageType.equals("connect")) {
            JsonObject returnObject = new JsonObject();
            List<String> joinedList = new ArrayList<>();
            nickname = "fucker";
            listeners.forEach(e -> {
                if (!e.getNickname().equals(this.nickname)) {
                    joinedList.add(e.getNickname());
                }
            });
            returnObject.addProperty("type", "joinedList");
            returnObject.addProperty("joinedList", joinedList.toString());

            try {
                sendToMe(returnObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (messageType.equals("message")) {
            String msg = parsedMessage.get("message").getAsString();

            JsonObject returnObject = new JsonObject();
            returnObject.addProperty("nick", nickname);
            returnObject.addProperty("msg", msg);

            try {
                sendToOthers("message", returnObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        listeners.remove(this);
        logger.info("onError called");
        logger.info(throwable.getMessage());
    }
}