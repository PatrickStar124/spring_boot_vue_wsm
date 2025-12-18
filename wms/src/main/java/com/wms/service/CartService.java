package com.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.common.Result;
import com.wms.entity.Cart;
import org.springframework.transaction.annotation.Transactional;

public interface CartService extends IService<Cart> {
    Result addToCart(Integer userId, Integer bookId);

    Result removeFromCart(Integer userId, Integer bookId);

    Result getCartByUser(Integer userId);

}
