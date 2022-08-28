package com.huweiv.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huweiv.dto.SetmealDto;
import com.huweiv.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public Page<SetmealDto> getPage(int page, int pageSize, String name);

    public void saveSetmealWithDish(SetmealDto setmealDto);

    public SetmealDto getSetmealWithDish(Long id);

    public void updateSetmealWithDish(SetmealDto setmealDto);

    public void removeSetmealWithDish(List<Long> ids);
}
