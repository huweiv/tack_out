package com.huweiv.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huweiv.entity.Category;
import com.huweiv.entity.Dish;
import com.huweiv.entity.Setmeal;
import com.huweiv.exception.BusinessException;
import com.huweiv.mapper.CategoryMapper;
import com.huweiv.mapper.DishMapper;
import com.huweiv.mapper.SetmealMapper;
import com.huweiv.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName CategoryServiceImpl
 * @Description TODO
 * @CreateTime 2022/7/16 11:01
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * @title remove
     * @description 根据id删除分类，删除前需要进行判断
     * @author HUWEIV
     * @date 2022/7/16 19:25
     * @return void
     */
    @Override
    public void removeCategory(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        Integer dishCount = dishMapper.selectCount(dishLambdaQueryWrapper);
        if (dishCount > 0)
            throw new BusinessException("该分类下已关联菜品, 不能删除");
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        Integer setmealCount = setmealMapper.selectCount(setmealLambdaQueryWrapper);
        if (setmealCount > 0)
            throw new BusinessException("该分类下已关联套餐, 不能删除");
        categoryMapper.deleteById(id);
    }
}
