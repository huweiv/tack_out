package com.huweiv.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huweiv.entity.Category;

public interface CategoryService extends IService<Category> {
    public void removeCategory(Long id);
}
