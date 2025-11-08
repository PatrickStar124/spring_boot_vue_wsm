package com.example.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wms.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    List<User> listAll();
}
