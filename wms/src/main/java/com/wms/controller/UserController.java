package com.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wms.common.Result;
import com.wms.entity.User;
import com.wms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //  用户登录
    @PostMapping("/login")
        public Result login(@RequestBody User user){
        return userService.login(user.getNo(),user.getPassword());
    }

    //  用户注册
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        return userService.register(user);
    }
    @GetMapping("/info")
    public Result getInfo(@RequestParam Integer id){
        User user=userService.getUserInfo(id);
        return user !=null ?Result.suc(user):Result.fail("用户名不存在");
    }
}