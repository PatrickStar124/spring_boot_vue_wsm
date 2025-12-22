package com.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName("book")
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    // 关键：为id字段指定JDBC类型
    @TableId(type = IdType.AUTO)
    @TableField(value = "id", jdbcType = JdbcType.NUMERIC)  // 或 JdbcType.INTEGER
    private Integer id;

    private String name;
    private String author;
    private BigDecimal price;
    private Integer stock;

    @TableField(value = "image_url")
    private String imageUrl;
}