package com.example.ecomobile.controller;

import com.example.ecomobile.dto.*;
import com.example.ecomobile.entity.Chats;
import com.example.ecomobile.entity.Messages;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.repo.ChatsRepository;
import com.example.ecomobile.repo.MessagesRepository;
import com.example.ecomobile.repo.UserRepository;
import com.example.ecomobile.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ChatController {
    private static final Logger logger = Logger.getLogger(ChatController.class.getName());

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ChatsRepository chatsRepository;
    private final MessagesRepository messagesRepository;

    @Autowired
    public ChatController(ChatService chatService, UserRepository userRepository, ChatsRepository chatsRepository, MessagesRepository messagesRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.chatsRepository = chatsRepository;
        this.messagesRepository = messagesRepository;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> startChat(@RequestBody ChatRequest chatRequest) {
        try {
            logger.info("Received chat request: " + chatRequest.getProductId());



            Integer productId = chatRequest.getProductId();
            if (productId == null) {
                logger.warning("Invalid productId: null");
                return ResponseEntity.badRequest().body(Map.of("error", "Product ID is required"));
            }
            User user = userRepository.findById(chatRequest.getUserId()).orElseThrow();
            logger.info("Starting chat for product ID: " + productId + " and user: " + (user != null ? user.getId() : "null"));

            // Agar user null bo'lsa (autentifikatsiya qilinmagan)
            if (user == null) {
                logger.warning("User is not authenticated");
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            Chats chat = chatService.findOrCreateChat(user, productId);
            logger.info("Chat created/found with ID: " + chat.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("chatId", chat.getId());
            response.put("sellerId", chat.getSeller().getId());
            response.put("customerId", chat.getCustomer().getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Error starting chat: " + e.getMessage());
            e.printStackTrace(); // Stack trace ni log qilish
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/chat/{chatId}/test-message")
    public ResponseEntity<?> testSaveMessage(@PathVariable Integer chatId,
                                             @RequestParam Integer userId,
                                             @RequestParam String text) {
        try {
            logger.info("Test message request: chatId=" + chatId + ", userId=" + userId + ", text=" + text);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            Messages savedMessage = chatService.saveMessage(chatId, user, text);


            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "messageId", savedMessage.getId(),
                    "chatId", savedMessage.getChat().getId(),
                    "senderId", savedMessage.getSender().getId(),
                    "content", savedMessage.getContent(),
                    "sentAt", savedMessage.getSentAt()
            ));
        } catch (Exception e) {
            logger.severe("Error in test message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/chats/customers")
    public List<SellerDTO> getCustomersChats(@RequestParam Integer userId) {
        // Bu yerda userId ni query paramsdan olasiz

        User customer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Chats> chats = chatsRepository.findByCustomer(customer);

        return chats.stream()
                .map(chat -> {
                    User seller = chat.getSeller();
                    Messages lastMessage = chat.getMessages().stream()
                            .max(Comparator.comparing(Messages::getSentAt))
                            .orElse(null);

                    return new SellerDTO(
                            seller.getId(),
                            seller.getFirstname(), // yoki seller.getShopName()
                            lastMessage != null ? lastMessage.getContent() : "",
                            lastMessage != null ? lastMessage.getSentAt().toString() : ""
                    );
                })
                .collect(Collectors.toList());
    }


    @GetMapping("/history")
    public ResponseEntity<List<MessageDTO>> getChatHistory(
            @RequestParam Integer sellerId,
            @RequestParam Integer userId
    ) {
        System.out.println("SellerId: " + sellerId + ", UserId: " + userId);  // Debug uchun

        User customer = userRepository.findById(userId)
                .orElse(null);
        if (customer == null) {
            return ResponseEntity.badRequest().build(); // 400 - noto'g'ri userId
        }

        User seller = userRepository.findById(sellerId)
                .orElse(null);
        if (seller == null) {
            return ResponseEntity.badRequest().build(); // 400 - noto'g'ri sellerId
        }

        Chats chat = chatsRepository.findByCustomerAndSeller(customer, seller)
                .orElse(null);
        if (chat == null) {
            // Chat topilmasa, oddiy bo'sh massiv qaytaramiz
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<MessageDTO> messages = chat.getMessages().stream()
                .map(message -> new MessageDTO(
                        message.getSender().getId(),
                        message.getContent(),
                        message.getSentAt().toString()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(messages);
    }

    @PostMapping("/chats/send")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessageDTO sendMessageDTO) {
        Integer sellerId = sendMessageDTO.getSellerId();
        Integer customerId = sendMessageDTO.getCustomerId();
        String messageContent = sendMessageDTO.getMessage();

        // Avval customer va seller obyektlarini topamiz
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));


        // Avval chats bazasidan bor-yo'qligini tekshiramiz
        Chats chat = chatsRepository.findByCustomerAndSeller(customer, seller)
                .orElseGet(() -> {
                    Chats newChat = new Chats();
                    newChat.setCustomer(customer);
                    newChat.setSeller(seller);
                    return chatsRepository.save(newChat);
                });

        // Yangi message obyektini yaratamiz
        Messages newMessage = new Messages();
        newMessage.setContent(messageContent);
        newMessage.setChat(chat); // MUHIM: chat bilan bog'lash
        newMessage.setSender(customer);

        // Xabarni saqlaymiz
        messagesRepository.save(newMessage);

        // Javob sifatida xabarni qaytarishimiz mumkin
        return ResponseEntity.ok(newMessage);
    }

    @GetMapping("/chats/new-messages")
    public ResponseEntity<?> getChatNewMessages(@RequestParam Integer currentSellerId,
                                                @RequestParam Integer lastMessageId,
                                                @RequestParam Integer userId) {
        // Seller va Customer orasidagi chatni topish
        Chats chat = chatsRepository.findByCustomerIdAndSellerId(userId, currentSellerId);

        // Agar chat topilsa, yangi xabarlarni olish
        if (chat != null) {
            List<Messages> newMessages = messagesRepository.findByChatIdAndIdGreaterThanOrderBySentAtAsc(chat.getId(), lastMessageId);

            // Yangi xabarlarni DTO formatida qaytarish
            List<MessagesDTO> messageDTOs = newMessages.stream().map(message -> new MessagesDTO(
                    message.getId(),
                    message.getContent(),
                    message.getSender().getId(),
                    message.getSentAt()
            )).collect(Collectors.toList());

            return ResponseEntity.ok(messageDTOs);
        }

        return ResponseEntity.noContent().build(); // Agar chat topilmasa
    }

    @GetMapping("/chats/sellers")
    public List<CustomerDTO> getSellersChats(@RequestParam Integer userId) {
        // Bu yerda userId sotuvchining ID'si
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Chats> chats = chatsRepository.findBySeller(seller);

        return chats.stream()
                .map(chat -> {
                    User customer = chat.getCustomer();
                    Messages lastMessage = chat.getMessages().stream()
                            .max(Comparator.comparing(Messages::getSentAt))
                            .orElse(null);

                    return new CustomerDTO(
                            customer.getId(),
                            customer.getFirstname(),
                            lastMessage != null ? lastMessage.getContent() : "",
                            lastMessage != null ? lastMessage.getSentAt().toString() : ""
                    );
                })
                .collect(Collectors.toList());
    }

}