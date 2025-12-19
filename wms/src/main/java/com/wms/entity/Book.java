package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;


@Data
@TableName("book")
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;             // 图书ID（必须）

    private String name;            // 书名（必须）
    private String author;          // 作者（必须）
    private BigDecimal price;       // 价格（必须，购物车计算核心）
    private Integer stock;          // 库存（必须，防止超卖）

    private String imageUrl;


}