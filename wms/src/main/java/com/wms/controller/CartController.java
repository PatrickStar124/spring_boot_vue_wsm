// src/main/java/com/wms/controller/CartController.java
package com.wms.controller;

import com.wms.common.Result;
import com.wms.service.CartService;
import com.wms.service.CartService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public Result addToCart(@RequestParam Integer userId,
                            @RequestParam Integer bookId) {
        return cartService.addToCart(userId,bookId);
    }

    @DeleteMapping("/remove")
    public Result removeFromCart(@RequestParam Integer userId,
                                 @RequestParam Integer bookId) {
        return cartService.removeFromCart(userId,bookId);
    }

    @GetMapping("/list")
    public Result getCart(@RequestParam Integer userId) {
        return cartService.getCartByUser(userId);
    }

}