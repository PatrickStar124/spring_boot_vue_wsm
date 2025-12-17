// src/main/java/com/wms/controller/BookController.java
package com.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wms.common.Result;
import com.wms.entity.Book;
import com.wms.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/list")
    public Result list() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id){
        return bookService.getBookById(id);
    }

    @PostMapping("/save")
    public Result save(@RequestBody Book book){
        return bookService.addBook(book);
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        return  bookService.deleteBook(id);
    }

}