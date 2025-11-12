package com.wms.mapper;

import com.wms.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author wms
 * @since 2025-11-12
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
