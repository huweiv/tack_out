package com.huweiv.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huweiv.common.BaseContext;
import com.huweiv.common.R;
import com.huweiv.dto.OrdersDto;
import com.huweiv.entity.Orders;
import com.huweiv.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName OrderController
 * @Description TODO
 * @CreateTime 2022/8/28 11:03
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/list")
    public R<List<Orders>> list() {
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getUserId, BaseContext.getCurrentId());
        List<Orders> ordersList = orderService.list(lqw);
        return R.success(ordersList);
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize) {
        Page<OrdersDto> ordersDtoPage = orderService.getPage(page, pageSize);
        return R.success(ordersDtoPage);
    }
}
