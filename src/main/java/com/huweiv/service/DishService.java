package com.huweiv.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huweiv.dto.DishDto;
import com.huweiv.entity.Dish;
import java.util.List;


public interface DishService extends IService<Dish> {

    public void saveDishWithFlavor(DishDto dishDto);

    public Page<DishDto> getPage(int page, int pageSize, String name);

    public DishDto getDishWithFlavorById(Long id);

    public void updateDishWithFlavor(DishDto dishDto);

    public void removeDishWithFlavor(List<Long> ids);

    public List<DishDto> getList(Dish dish);
}
