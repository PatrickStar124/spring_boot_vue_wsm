package com.example.wms.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.example.wms.entity.User;
import com.example.wms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class HellowController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @Autowired
    private UserService userService;

    @GetMapping("/user/list")
    public List<User> listUsers() {
        //return userService.list();
        return userService.listAll();
    }
}