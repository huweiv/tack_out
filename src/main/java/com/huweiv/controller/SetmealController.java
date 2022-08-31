package com.huweiv.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huweiv.common.R;
import com.huweiv.dto.SetmealDto;
import com.huweiv.entity.Setmeal;
import com.huweiv.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName SetmealController
 * @Description TODO
 * @CreateTime 2022/7/19 21:14
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/page")
    public R<Page> getPage(int page, int pageSize, String name) {
        Page<SetmealDto> setmealDtoPage = setmealService.getPage(page, pageSize, name);
        return R.success(setmealDtoPage);
    }

    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveSetmealWithDish(setmealDto);
        return R.success("新增成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> edit(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getSetmealWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateSetmealWithDish(setmealDto);
        return R.success("修改成功");
    }

    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> statusHandle(@PathVariable int status, @RequestParam List<Long> ids) {
        List<Setmeal> setmealList = ids.stream().map((item) -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setStatus(status);
            setmeal.setId(item);
            return setmeal;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(setmealList);
        return R.success("更改成功");
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.removeSetmealWithDish(ids);
        return R.success("删除成功");
    }

    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "'setmeal_' + #setmeal.getCategoryId() + '_' + #setmeal.getStatus()")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        lqw.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        lqw.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(lqw);
        return R.success(setmealList);
    }
}
