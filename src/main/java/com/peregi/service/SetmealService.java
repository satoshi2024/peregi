package com.peregi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peregi.dto.SetmealDto;
import com.peregi.entity.Setmeal;

import java.util.List;
/**
 * @Author:Chikai_Cho
 * @Date 2024/03/20 19:54
 * @Version 1.0
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);
}
