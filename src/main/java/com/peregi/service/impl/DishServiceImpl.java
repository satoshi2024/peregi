package com.peregi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peregi.dto.DishDto;
import com.peregi.entity.Dish;
import com.peregi.entity.DishFlavor;
import com.peregi.mapper.DishMapper;
import com.peregi.service.DishFlavorService;
import com.peregi.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author:Chikai_Cho
 * @Date 2024/03/20 19:59
 * @Version 1.0
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新しい料理を追加し、それに対応する味のデータも保存
     *
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //料理の基本情報を料理テーブル（dish）に保存
        this.save(dishDto);

        Long dishId = dishDto.getId();//料理のID

        //料理の味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //料理の味のデータを料理の味のテーブル（dish_flavor）に保存
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * IDに基づいて料理情報と対応する味の情報を検索
     *
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id) {
        //料理の基本情報を検索します。dishテーブルから検索
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //現在の料理に対応する味の情報を検索します。dish_flavorテーブルから検索
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //現在の料理に関連する味のデータを削除します。dish_flavorテーブルの削除操作を行い
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //現在の送信された味のデータを追加します。dish_flavorテーブルの挿入操作を行い
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
