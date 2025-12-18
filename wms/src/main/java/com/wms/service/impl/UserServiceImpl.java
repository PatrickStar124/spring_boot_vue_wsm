package com.wms.service.impl;

// 新增这些导入
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wms.common.Result;
import com.wms.entity.User;
import com.wms.mapper.UserMapper;
import com.wms.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wms
 * @since 2025-11-12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Result login(String no, String password) {
        // 原来的登录逻辑从Controller移到这里
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("no", no)
                .eq("password", password);

        User loginUser = this.getOne(wrapper);
        if (loginUser != null) {
            // 可以在这里添加更多业务逻辑，如记录登录日志等
            return Result.suc(loginUser);
        }
        return Result.fail("用户名或密码错误");
    }

    @Override
    public Result register(User user) {
        // 检查用户名是否存在的逻辑移到这里
        if (this.checkUserExists(user.getNo())) {
            return Result.fail("用户名已存在");
        }

        boolean saved = this.save(user);
        return saved ? Result.suc("注册成功") : Result.fail("注册失败");
    }

    @Override
    public User getUserInfo(Integer id) {
        // 可以在这里添加权限检查等逻辑
        return this.getById(id);
    }

    private boolean checkUserExists(String no) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("no", no);
        return this.count(wrapper) > 0;
    }
}