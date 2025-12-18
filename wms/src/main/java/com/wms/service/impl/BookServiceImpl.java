// src/main/java/com/wms/service/impl/BookServiceImpl.java
package com.wms.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.common.Result;
import com.wms.controller.CartController;
import com.wms.entity.Book;
import com.wms.mapper.BookMapper;
import com.wms.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    private final CartController cartController;

    public BookServiceImpl(CartController cartController) {
        this.cartController = cartController;
    }

    @Override
    public Result getAllBooks() {
        try{
            List<Book> books = this.list();
            return  Result.suc(books);
        }catch (Exception e){
            return Result.fail("查询失败"+e.getMessage());
        }
    }

    @Override
    public Result getBookById(Integer id) {
        try{
            Book book =this.getById(id);
            return book !=null ? Result.suc(book):Result.fail("图书不存在");
        }catch (Exception e){
            return Result.fail("查询失败："+e.getMessage());
        }
    }

    @Override
    public Result addBook(Book book) {
        try {
            // 简单的数据验证
            if (book.getName() == null || book.getName().trim().isEmpty()) {
                return Result.fail("书名不能为空");
            }
            if (book.getPrice() == null || book.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
                return Result.fail("价格不能为负数");
            }

            boolean saved = this.save(book);
            return saved ? Result.suc("新增成功", book) : Result.fail("新增失败");
        } catch (Exception e) {
            return Result.fail("新增失败: " + e.getMessage());
        }
    }

    @Override
    public Result deleteBook(Integer id) {
        try {
            boolean removed = this.removeById(id);
            return removed ? Result.suc("删除成功") : Result.fail("删除失败或图书不存在");
        } catch (Exception e) {
            return Result.fail("删除失败: " + e.getMessage());
        }
    }
}