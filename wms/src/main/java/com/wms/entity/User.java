package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("\"USER\"")  // ！！！重要：Oracle需要双引号！！！
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private Integer id;

    private String no;
    private String name;
    private String password;
    private String isValid = "Y";
}