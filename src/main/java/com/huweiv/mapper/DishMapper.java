package com.huweiv.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huweiv.dto.DishDto;
import com.huweiv.entity.Dish;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    @Select("select * from dish where id = #{id}")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "name", property = "name"),
            @Result(column = "category_id", property = "categoryId"),
            @Result(column = "price", property = "price"),
            @Result(column = "code", property = "code"),
            @Result(column = "image", property = "image"),
            @Result(column = "image", property = "image"),
            @Result(column = "description", property = "description"),
            @Result(column = "status", property = "status"),
            @Result(column = "sort", property = "sort"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime"),
            @Result(column = "create_user", property = "createUser"),
            @Result(column = "update_user", property = "updateUser"),
            @Result(column = "is_deleted", property = "isDeleted"),
            @Result(
                    column = "id",
                    property = "flavors",
                    javaType = List.class,
                    many = @Many(select = "com.huweiv.mapper.DishFlavorMapper.selectByDishId")
            )
    })
    public DishDto selectDishWithFlavorById(Long id);
}
