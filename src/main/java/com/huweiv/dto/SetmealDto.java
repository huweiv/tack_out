package com.huweiv.dto;

import com.huweiv.entity.Setmeal;
import com.huweiv.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
