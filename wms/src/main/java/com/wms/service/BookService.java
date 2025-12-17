// src/main/java/com/wms/service/BookService.java
package com.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.common.Result;
import com.wms.entity.Book;

public interface BookService extends IService<Book> {

    Result getAllBooks();

    Result getBookById(Integer id);

    Result addBook(Book book);

    Result deleteBook(Integer id);

}