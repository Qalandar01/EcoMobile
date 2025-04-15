package com.example.ecomobile.config;

import com.example.ecomobile.entity.Messages;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.repo.UserRepository;
import com.example.ecomobile.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private static final Logger logger = Logger.getLogger(WebSocketConfig.class.getName());

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public ChatWebSocketHandler chatWebSocketHandler() {
        return new ChatWebSocketHandler();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler(), "/chat")
                .setAllowedOrigins("*"); // Development uchun, production muhitida aniq domainlarni ko'rsating
    }

    public class ChatWebSocketHandler extends TextWebSocketHandler {
        private final Map<Integer, List<WebSocketSession>> chatSessions = new ConcurrentHashMap<>();
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            try {
                Integer chatId = getChatId(session);
                logger.info("New WebSocket connection established for chat ID: " + chatId);

                chatSessions.computeIfAbsent(chatId, k -> new ArrayList<>()).add(session);

                // Ulanish muvaffaqiyatli bo'lganini xabar berish
                session.sendMessage(new TextMessage("{\"type\":\"connection\",\"status\":\"connected\"}"));
            } catch (Exception e) {
                logger.severe("Error in connection establishment: " + e.getMessage());
                session.close(CloseStatus.BAD_DATA.withReason("Invalid chat ID"));
            }
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            try {
                Integer chatId = getChatId(session);
                logger.info("Received message for chat ID: " + chatId + ", content: " + message.getPayload());

                // Xabarni JSON dan o'qish
                Map<String, Object> messageData = objectMapper.readValue(message.getPayload(), Map.class);

                // Sender va text ni olish
                Object senderObj = messageData.get("sender");
                String text = (String) messageData.get("text");


                // Sender ni Long ga o'girish
                Integer userId;
                if (senderObj instanceof Number) {
                    userId = ((Number) senderObj).intValue();
                } else if (senderObj instanceof String) {
                    try {
                        userId = Integer.parseInt((String) senderObj);
                    } catch (NumberFormatException e) {
                        logger.severe("Invalid sender ID format: " + senderObj);
                        return;
                    }
                } else {
                    logger.severe("Invalid sender type: " + (senderObj != null ? senderObj.getClass().getName() : "null"));
                    return;
                }

                logger.info("Parsed user ID: " + userId + ", text: " + text);

                // Foydalanuvchini topish
                User user = userRepository.findById(userId).orElse(null);

                if (user == null) {
                    logger.severe("User not found with ID: " + userId);
                    return;
                }

                if (text == null || text.isEmpty()) {
                    logger.warning("Empty message text");
                    return;
                }

                try {
                    // Xabarni ma'lumotlar bazasiga saqlash
                    Messages savedMessage = chatService.saveMessage(chatId, user, text);
                    logger.info("Message saved to database with ID: " + savedMessage.getId());

                    // Xabarni boshqa foydalanuvchilarga yuborish
                    List<WebSocketSession> sessions = chatSessions.get(chatId);
                    if (sessions != null) {
                        for (WebSocketSession s : sessions) {
                            if (s.isOpen() && !s.equals(session)) {
                                s.sendMessage(message);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.severe("Error saving message: " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                logger.severe("Error handling message: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
            try {
                Integer chatId = getChatId(session);
                logger.info("WebSocket connection closed for chat ID: " + chatId + ", status: " + status);

                List<WebSocketSession> sessions = chatSessions.get(chatId);
                if (sessions != null) {
                    sessions.remove(session);
                    if (sessions.isEmpty()) {
                        chatSessions.remove(chatId);
                    }
                }
            } catch (Exception e) {
                logger.warning("Error during connection close: " + e.getMessage());
            }
        }

        private Integer getChatId(WebSocketSession session) {
            String query = session.getUri().getQuery();
            if (query == null || !query.contains("chatId=")) {
                throw new IllegalArgumentException("Missing chatId parameter");
            }
            return Integer.parseInt(query.split("chatId=")[1]);
        }

        // Xabar yuborish uchun qo'shimcha metod
        public void sendMessageToChat(Long chatId, String message) throws IOException {
            List<WebSocketSession> sessions = chatSessions.get(chatId);
            if (sessions != null) {
                TextMessage textMessage = new TextMessage(message);
                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                    }
                }
            }
        }
    }
}
