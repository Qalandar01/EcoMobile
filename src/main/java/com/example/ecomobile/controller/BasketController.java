package com.example.ecomobile.controller;

import com.example.ecomobile.custom.BasketItem;
import com.example.ecomobile.service.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/basket")
public class BasketController {

    @Autowired
    private BasketService basketService;
    @PostMapping("/add")
    public String addToBasket(@RequestParam Integer userId, @RequestBody BasketItem basketItem) {
        basketService.addItemToBasket(userId, basketItem);
        return "Mahsulot savatchaga qo'shildi";
    }

    @GetMapping("/{userId}")
    public Object getBasket(@PathVariable Integer userId) {
        return basketService.getBasket(userId);
    }

    @DeleteMapping("/{userId}/item/{productId}")
    public String removeItem(@PathVariable Integer userId, @PathVariable Integer productId) {
        basketService.removeItemFromBasket(userId, productId);
        return "Mahsulot savatchadan o'chirildi";
    }

}
