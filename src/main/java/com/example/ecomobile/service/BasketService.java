package com.example.ecomobile.service;

import com.example.ecomobile.custom.Basket;
import com.example.ecomobile.custom.BasketItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BasketService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String BASKET_PREFIX = "basket:";

    // Foydalanuvchi ID bo‘yicha savatchani olish
    public Basket getBasket(String userId) {
        Basket basket = (Basket) redisTemplate.opsForValue().get(BASKET_PREFIX + userId);
        if (basket == null) {
            basket = new Basket(userId);
        }
        return basket;
    }

    // Savatchani Redis ga saqlash
    public void saveBasket(Basket basket) {
        redisTemplate.opsForValue().set(BASKET_PREFIX + basket.getUserId(), basket);
        // Agar kerak bo‘lsa, TTL (Time To Live) qo‘shish mumkin:
        // redisTemplate.expire(BASKET_PREFIX + basket.getUserId(), 1, TimeUnit.DAYS);
    }

    // Savatchaga mahsulot qo‘shish (agar mahsulot bor bo'lsa miqdorni oshirish)
    public void addItemToBasket(String userId, BasketItem newItem) {
        Basket basket = getBasket(userId);
        Optional<BasketItem> existingItem = basket.getItems().stream()
                .filter(item -> item.getProductId().equals(newItem.getProductId()))
                .findFirst();
        if (existingItem.isPresent()) {
            BasketItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + newItem.getQuantity());
        } else {
            basket.getItems().add(newItem);
        }
        saveBasket(basket);
    }

    // Savatchadan mahsulotni olib tashlash (ixtiyoriy)
    public void removeItemFromBasket(String userId, Long productId) {
        Basket basket = getBasket(userId);
        basket.getItems().removeIf(item -> item.getProductId().equals(productId));
        saveBasket(basket);
    }

    // Savatchani tozalash
    public void clearBasket(String userId) {
        redisTemplate.delete(BASKET_PREFIX + userId);
    }
}