package com.peregi.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;


    //料理名
    private String name;


    //料理カテゴリID
    private Long categoryId;


    //価格
    private BigDecimal price;


    //商品コード
    private String code;


    //写真
    private String image;


    //説明情報
    private String description;


    //0 販売停止 1 販売再開
    private Integer status;


    //順序
    private Integer sort;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
