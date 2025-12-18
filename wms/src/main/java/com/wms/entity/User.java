package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("user")  // 对应数据库表名1
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;        // 主键ID

    private String no;         // 账号（用户名）
    private String name;       // 显示名称
    private String password;   // 密码

    // 可选：保留一个状态字段
    private String isValid = "Y";  // 是否有效，默认Y
}