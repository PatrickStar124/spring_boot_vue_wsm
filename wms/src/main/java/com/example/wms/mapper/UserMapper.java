package com.example.wms.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.wms.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<User> listAll();
}