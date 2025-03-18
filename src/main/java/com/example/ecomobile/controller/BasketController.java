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
    public String addToBasket(@RequestParam String userId, @RequestBody BasketItem basketItem) {
        basketService.addItemToBasket(userId, basketItem);
        return "Mahsulot savatchaga qo'shildi";
    }

    @GetMapping("/{userId}")
    public Object getBasket(@PathVariable String userId) {
        return basketService.getBasket(userId);
    }

    @DeleteMapping("/{userId}")
    public String clearBasket(@PathVariable String userId) {
        basketService.clearBasket(userId);
        return "Savatcha tozalandi";
    }
}
