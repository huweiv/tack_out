package com.huweiv.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huweiv.common.R;
import com.huweiv.dto.DishDto;
import com.huweiv.entity.Dish;
import com.huweiv.entity.DishFlavor;
import com.huweiv.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName DishController
 * @Description TODO
 * @CreateTime 2022/7/18 21:46
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<DishDto> dishDtoPage = dishService.getPage(page, pageSize, name);
        return R.success(dishDtoPage);
    }

    @PostMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveDishWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("新增成功");
    }

    @GetMapping("/{id}")
    public R<DishDto> edit(@PathVariable Long id) {
        DishDto dishDto = dishService.getDishWithFlavorById(id);
        return R.success(dishDto);
    }

    @PutMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateDishWithFlavor(dishDto);
        return R.success("修改成功");
    }

    @PostMapping("/status/{status}")
    @CacheEvict(value = "dishCache", allEntries = true)
    public R<String> statusHandle(@PathVariable int status, @RequestParam List<Long> ids) {
        List<Dish> dishList = ids.stream().map((item) -> {
            Dish dish = new Dish();
            dish.setId(item);
            dish.setStatus(status);
            return dish;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishList);
        return R.success("更改成功");
    }

    @DeleteMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids) {
        dishService.removeDishWithFlavor(ids);
        return R.success("删除成功");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
//        Long categoryId = dish.getCategoryId();
//        String name = dish.getName();
//        lqw.eq(categoryId != null, Dish::getCategoryId, categoryId);
//        lqw.like(StringUtils.isNotEmpty(name), Dish::getName, name);
//        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> dishList = dishService.list(lqw);
//        return R.success(dishList);
//    }

    @GetMapping("/list")
    @Cacheable(value = "dishCache", key = "'dish_' + #dish.getCategoryId() + '_' + #dish.getStatus()")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = dishService.getList(dish);
        return R.success(dishDtoList);
    }

}
