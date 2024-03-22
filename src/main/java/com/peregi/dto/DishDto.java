package com.peregi.dto;

import com.peregi.entity.Dish;
import com.peregi.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
/**
 * @Author:Chikai_Cho
 * @Date 2024/03/21 14:13
 * @Version 1.0
 */
@Data
public class DishDto extends Dish {

    //料理に対応する風味データ
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
