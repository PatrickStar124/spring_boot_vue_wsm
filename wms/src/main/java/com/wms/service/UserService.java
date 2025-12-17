package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.Result;
import com.wms.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类1
 * </p>
 *
 * @author wms
 * @since 2025-11-12
 */
public interface UserService extends IService<User> {
    Result login(String no, String password);
    Result register(User user);
    User getUserInfo(Integer id);
}
