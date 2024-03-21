package com.peregi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peregi.common.CustomException;
import com.peregi.entity.Category;
import com.peregi.entity.Dish;
import com.peregi.entity.Setmeal;
import com.peregi.mapper.CategoryMapper;
import com.peregi.service.CategoryService;
import com.peregi.service.DishService;
import com.peregi.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * @Author:Chikai_Cho
 * @Date 2024/03/20 14:13
 * @Version 1.0
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * IDに基づいてカテゴリを削除する前に、削除するかどうかを判断する必要があり
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //カテゴリIDに基づいてクエリ条件を追加して検索
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        //現在のカテゴリが料理に関連付けられているかどうかを検索し、関連付けられている場合は業務例外をスローし
        if(count1 > 0){
            //料理がすでに関連付けられています。業務例外をスローし
            throw new CustomException("現在のカテゴリには料理が関連付けられています。削除できません");
        }

        //現在のカテゴリが定食に関連付けられているかどうかを検索し、関連付けられている場合は業務例外をスローし
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //クエリー条件の追加、カテゴリーIDによるクエリー
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count();
        if(count2 > 0){
            //すでに関連付けられている場合は、ビジネス例外をスローする
            throw new CustomException("現在のカテゴリには定食が関連付けられています。削除できません");
        }

        //カテゴリを正常に削除
        super.removeById(id);
    }
}
