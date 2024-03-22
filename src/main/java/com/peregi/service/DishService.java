package com.peregi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peregi.dto.DishDto;
import com.peregi.entity.Dish;
/**
 * @Author:Chikai_Cho
 * @Date 2024/03/20 19:59
 * @Version 1.0
 */
public interface DishService extends IService<Dish> {

    //新しい料理を追加し、同時に料理に対応する味のデータを挿入する必要があります。この操作では、dishテーブルとdish_flavorテーブルの両方に対して操作を行い
    public void saveWithFlavor(DishDto dishDto);

    //IDに基づいて料理情報と対応する味の情報を検索
    public DishDto getByIdWithFlavor(Long id);

    //料理情報を更新し、同時に対応する味の情報も更新
    public void updateWithFlavor(DishDto dishDto);
}
