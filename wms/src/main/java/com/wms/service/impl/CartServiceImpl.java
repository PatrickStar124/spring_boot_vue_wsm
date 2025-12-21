package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.common.Result;
import com.wms.entity.Book;
import com.wms.entity.Cart;
import com.wms.mapper.BookMapper;
import com.wms.mapper.CartMapper;
import com.wms.service.BookService;
import com.wms.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookService bookService;


    @Override
    @Transactional
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

            //3.减少book类对应的库存
            book.setStock(book.getStock()-1);
            bookMapper.updateById(book);

            BigDecimal currentPrice = book.getPrice();

            // 4. 检查是否已在购物车
            QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("book_id", bookId);
            Cart existingCart = this.getOne(queryWrapper);

            if(existingCart != null){
                // 5. 如果已存在，数量+1
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
    @Transactional
    public Result removeFromCart(Integer userId, Integer bookId) {
        try{
            QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("book_id", bookId);

            //1.先获取购物车记录，知道要恢复多少库存
            Cart cart = this.getOne(queryWrapper);
            if (cart==null){
                return Result.fail("商品不在购物车当中");
            }

            int quantity = cart.getQuantity();

            //2.恢复库存
            Book book = bookMapper.selectById(bookId);
            if(book != null){
                book.setStock(book.getStock()+quantity);
                bookMapper.updateById(book);
            }

            //3.删除购物车记录
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

            for(Cart cart : cartItems){
                cart.setBook((Book)bookService.getBookById(cart.getBookId()).getData());
            }

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

    @Override
    @Transactional
    public Result checkout(Integer userId) {
        try {
            // 1. 获取用户的购物车列表
            QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            List<Cart> cartItems = this.list(queryWrapper);

            if (cartItems == null || cartItems.isEmpty()) {
                return Result.fail("购物车为空，无法结算");
            }

            // 2. 准备结算信息
            List<Map<String, Object>> checkoutItems = new ArrayList<>();
            BigDecimal totalPrice = BigDecimal.ZERO;
            int totalQuantity = 0;

            // 3. 记录结算的商品信息（但不恢复库存，因为库存已经在加入购物车时减少了）
            for (Cart cart : cartItems) {
                Integer bookId = cart.getBookId();
                Integer quantity = cart.getQuantity();

                if (bookId == null || quantity == null || quantity <= 0) {
                    return Result.fail("购物车数据异常，请刷新后重试");
                }

                // 查询图书信息
                Book book = bookMapper.selectById(bookId);
                if (book == null) {
                    return Result.fail("图书不存在，ID: " + bookId);
                }

                // 计算当前项总价
                BigDecimal itemPrice = cart.getPrice() != null ?
                        cart.getPrice() : book.getPrice();
                BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(quantity));

                totalPrice = totalPrice.add(itemTotal);
                totalQuantity += quantity;

                // 记录结算项信息
                Map<String, Object> item = new HashMap<>();
                item.put("bookId", bookId);
                item.put("bookName", book.getName());
                item.put("author", book.getAuthor());
                item.put("quantity", quantity);
                item.put("price", itemPrice);
                item.put("total", itemTotal);
                item.put("imageUrl", book.getImageUrl());
                checkoutItems.add(item);

                System.out.println("已结算: " + book.getName() + " × " + quantity +
                        " = ¥" + itemTotal);
            }

            // 4. 清空购物车（删除该用户的所有购物车项）
            QueryWrapper<Cart> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("user_id", userId);
            int deleted = this.remove(deleteWrapper) ? 1 : 0;

            if (deleted <= 0) {
                throw new RuntimeException("清空购物车失败");
            }

            System.out.println("用户 " + userId + " 的购物车已清空，删除了 " + cartItems.size() + " 条记录");

            // 5. 准备返回结果
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("checkoutItems", checkoutItems);
            resultData.put("totalQuantity", totalQuantity);
            resultData.put("totalPrice", totalPrice);
            resultData.put("itemCount", checkoutItems.size());
            resultData.put("timestamp", new Date());

            return Result.suc("结算成功！共结算 " + totalQuantity + " 件商品，总计 ¥" + totalPrice, resultData);

        } catch (Exception e) {
            System.err.println("结算失败: " + e.getMessage());
            e.printStackTrace();
            return Result.fail("结算失败: " + e.getMessage());
        }
    }

}