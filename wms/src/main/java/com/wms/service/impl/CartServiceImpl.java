package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.common.Result;
import com.wms.entity.Book;
import com.wms.entity.Cart;
import com.wms.mapper.BookMapper;
import com.wms.mapper.CartMapper;
import com.wms.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {
    @Autowired
    private BookMapper bookMapper;


    @Override
    public Result addToCart(Integer userId, Integer bookId) {
        try{
            // 1. 验证图书是否存在并获取价格
            Book book = bookMapper.selectById(bookId);
            if(book == null){
                return Result.fail("图书不存在");
            }
            // 2. 检查库存
            if (book.getStock()<=0){
                return Result.fail("库存不足");
            }
            BigDecimal currentPrice = book.getPrice();

            // 3. 检查是否已在购物车
            QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
            .eq("book_id", bookId);
            Cart existingCart = this.getOne(queryWrapper);

            if(existingCart != null){
                // 4. 如果已存在，数量+1
                existingCart.setQuantity(existingCart.getQuantity()+1);
                this.updateById(existingCart);
                return Result.suc("已增加数量",existingCart);
            }else {
                Cart newCart = new Cart();
                newCart.setUserId(userId);
                newCart.setBookId(bookId);
                newCart.setQuantity(1);
                newCart.setPrice(currentPrice);

                this.save(newCart);
                return Result.suc("加入购物车成功",newCart);
            }
        }catch (Exception e){
            return Result.fail("加入购物车失败"+e.getMessage());
        }

    }

    @Override
    public Result removeFromCart(Integer userId, Integer bookId) {
        try{
            QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("book_id", bookId);
            boolean removed = this.remove(queryWrapper);
            return removed ? Result.suc("移除购物车成功"):Result.fail("商品不在购物车当中");
        }catch (Exception e){
            return Result.fail("移出购物车失败"+e.getMessage());
        }
    }

    @Override
    public Result getCartByUser(Integer userId) {
        try{
            QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);

            List<Cart> cartItems = this.list(queryWrapper);

            BigDecimal totalAmount = BigDecimal.ZERO;
            for (Cart cart : cartItems) {
                BigDecimal itemTotal = cart.getPrice().multiply(new BigDecimal(cart.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
            return Result.suc(cartItems,totalAmount.longValue());
        }catch (Exception e){
            return Result.fail("查询购物车失败"+e.getMessage());
        }
    }


}

