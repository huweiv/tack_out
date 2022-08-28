package com.huweiv.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huweiv.dto.DishDto;
import com.huweiv.entity.Category;
import com.huweiv.entity.Dish;
import com.huweiv.entity.DishFlavor;
import com.huweiv.exception.BusinessException;
import com.huweiv.mapper.CategoryMapper;
import com.huweiv.mapper.DishMapper;
import com.huweiv.service.DishFlavorService;
import com.huweiv.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName DishServiceImpl
 * @Description TODO
 * @CreateTime 2022/7/16 11:52
 */
@Service
@Transactional
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * @title saveWithFlavor
     * @description 保存菜品并保存菜品口味
     * @author HUWEIV
     * @date 2022/7/19 9:53
     * @return void
     */
    @Override
    public void saveDishWithFlavor(DishDto dishDto) {
        dishMapper.insert(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> dishFlavorList = dishDto.getFlavors();
        dishFlavorList = dishFlavorList.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(dishFlavorList);
    }

    @Override
    public Page<DishDto> getPage(int page, int pageSize, String name) {
        Page<Dish> dishPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishMapper.selectPage(dishPage, dishLambdaQueryWrapper);
        Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        List<Dish> dishList = dishPage.getRecords();
        List<DishDto> dishDtoList = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Category category = categoryMapper.selectById(item.getCategoryId());
            if (category != null)
                dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtoList);
        return dishDtoPage;
    }

    @Override
    public List<DishDto> getList(Dish dish) {
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        Long categoryId = dish.getCategoryId();
        String name = dish.getName();
        lqw.eq(categoryId != null, Dish::getCategoryId, categoryId);
        lqw.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishMapper.selectList(lqw);
        List<DishDto> dishDtoList = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Category category = categoryMapper.selectById(item.getCategoryId());
            if (category != null)
                dishDto.setCategoryName(category.getName());
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return dishDtoList;
    }

    @Override
    public DishDto getDishWithFlavorById(Long id) {
//        DishDto dishDto = dishMapper.selectDishWithFlavorById(id);
        Dish dish = dishMapper.selectById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(lqw);
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }

    @Override
    public void updateDishWithFlavor(DishDto dishDto) {
        dishMapper.updateById(dishDto);
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        Long dishId = dishDto.getId();
        lqw.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(lqw);
        List<DishFlavor> dishFlavorList = dishDto.getFlavors();
        dishFlavorList = dishFlavorList.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(dishFlavorList);
    }

    @Override
    public void removeDishWithFlavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId, ids).eq(Dish::getStatus, 1);
        Integer dishCount = dishMapper.selectCount(dishLambdaQueryWrapper);
        if (dishCount > 0)
            throw new BusinessException("菜品正在售卖中，请先停止售卖");
        dishMapper.deleteBatchIds(ids);
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
    }
}
