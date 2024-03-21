package com.peregi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peregi.common.R;
import com.peregi.entity.Category;
import com.peregi.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author:Chikai_Cho
 * @Date 2024/03/20 14:17
 * @Version 1.0
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新カテゴリー
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("カテゴリーを追加した");
    }

    /**
     * ページネーション検索
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //ページネーションビルダー
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //条件ビルダー
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //ソート条件を追加し、"sort"に基づいてソート
        queryWrapper.orderByAsc(Category::getSort);

        //ページネーションビルダー
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * IDに基づいてカテゴリを削除します
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id){
        log.info("指定されたID：{}",id);

        //categoryService.removeById(id);
        categoryService.remove(id);

        return R.success("カテゴリ情報が削除された");
    }

    /**
     * IDに基づいてカテゴリ情報を変更
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("カテゴリ情報を変更：{}",category);

        categoryService.updateById(category);

        return R.success("カテゴリ情報が正常に変更された");
    }

    /**
     * 条件に基づいてカテゴリデータを検索
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件ビルダー
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //条件を追加
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //並べ替え条件を追加
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
