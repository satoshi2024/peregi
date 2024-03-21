package com.peregi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peregi.entity.Category;
/**
 * @Author:Chikai_Cho
 * @Date 2024/03/20 14:13
 * @Version 1.0
 */
public interface CategoryService extends IService<Category> {
    public void remove(Long id);

}
