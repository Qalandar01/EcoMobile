package com.example.ecomobile.service;

import com.example.ecomobile.entity.Chats;
import com.example.ecomobile.entity.Messages;
import com.example.ecomobile.entity.Product;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.repo.ChatsRepository;
import com.example.ecomobile.repo.MessagesRepository;
import com.example.ecomobile.repo.ProductRepository;
import com.example.ecomobile.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.Logger;

@Service
public class ChatService {
    private static final Logger logger = Logger.getLogger(ChatService.class.getName());

    @Autowired
    private ChatsRepository chatRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MessagesRepository messagesRepository;
    @Autowired
    private UserRepository userRepository;

    public Chats findOrCreateChat(User user, Integer productId) {
        logger.info("Finding or creating chat for user: " + user.getId() + " and product: " + productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Mahsulot topilmadi!"));

        User seller = product.getUsers();

        // Foydalanuvchi o'zi sotuvchi bo'lgan mahsulot uchun chat yarata olmasligi kerak
        if (user.getId().equals(seller.getId())) {
            throw new RuntimeException("O'zingizning mahsulotingiz uchun chat yarata olmaysiz!");
        }

        return chatRepository.findByCustomerAndSeller(user, seller)
                .orElseGet(() -> {
                    logger.info("Creating new chat between customer: " + user.getId() + " and seller: " + seller.getId());

                    Chats newChat = new Chats();
                    newChat.setCustomer(user);
                    newChat.setSeller(seller);
                    newChat.setMessages(new ArrayList<>());
                    return chatRepository.save(newChat);
                });
    }

    public List<Map<String, Object>> getChatMessages(Integer chatId, User user) {
        logger.info("Getting messages for chat: " + chatId);

        Chats chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat topilmadi!"));

        // Faqat chat ishtirokchilari xabarlarni ko'ra olishi kerak
        if (!chat.getCustomer().getId().equals(user.getId()) && !chat.getSeller().getId().equals(user.getId())) {
            throw new RuntimeException("Bu chatni ko'rish huquqingiz yo'q!");
        }

        List<Messages> messages = messagesRepository.findByChatOrderBySentAtAsc(chat);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Messages message : messages) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("id", message.getId());
            messageMap.put("sender", message.getSender().getId().equals(user.getId()) ? "user" : "other");
            messageMap.put("text", message.getContent());
            messageMap.put("time", message.getSentAt());
            result.add(messageMap);
        }

        return result;
    }

    @Transactional
    public Messages saveMessage(Integer chatId, User sender, String content) {
        logger.info("Saving message for chat ID: " + chatId + ", sender: " + sender.getId() + ", content: " + content);

        try {
            Chats chat = chatRepository.findById(chatId)
                    .orElseThrow(() -> new RuntimeException("Chat topilmadi! ID: " + chatId));

            logger.info("Chat found: " + chat.getId() + ", customer: " + chat.getCustomer().getId() + ", seller: " + chat.getSeller().getId());


            // Faqat chat ishtirokchilari xabar yubora olishi kerak
            if (!chat.getCustomer().getId().equals(sender.getId()) && !chat.getSeller().getId().equals(sender.getId())) {
                logger.severe("User " + sender.getId() + " is not a participant in chat " + chatId);
                throw new RuntimeException("Bu chatga xabar yuborish huquqingiz yo'q!");
            }

            Messages message = new Messages();
            message.setChat(chat);
            message.setSender(sender);
            message.setContent(content);

            Messages savedMessage = messagesRepository.save(message);
            logger.info("Message saved with ID: " + savedMessage.getId());

            // If you're using Option 2 with junction table, you need to add this:
            // chat.getMessages().add(savedMessage);
            // chatRepository.save(chat);

            return savedMessage;
        } catch (Exception e) {
            logger.severe("Error saving message: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

}
