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

    public Basket getBasket(Integer userId) {
        Basket basket = (Basket) redisTemplate.opsForValue().get(BASKET_PREFIX + userId);
        if (basket == null) {
            basket = new Basket(userId);
        }
        return basket;
    }

    public void saveBasket(Basket basket) {
        redisTemplate.opsForValue().set(BASKET_PREFIX + basket.getUserId(), basket);
    }

    public void addItemToBasket(Integer userId, BasketItem newItem) {
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

    public void removeItemFromBasket(Integer userId, Integer productId) {
        Basket basket = getBasket(userId);
        basket.getItems().removeIf(item -> item.getProductId().equals(productId));
        saveBasket(basket);
    }

    public void clearBasket(Integer userId) {
        redisTemplate.delete(BASKET_PREFIX + userId);
    }
}