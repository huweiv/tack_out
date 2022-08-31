package com.huweiv.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huweiv.common.R;
import com.huweiv.dto.OrdersDto;
import com.huweiv.entity.Orders;
import com.huweiv.service.OrderDetailService;
import com.huweiv.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName OrderDetailController
 * @Description TODO
 * @CreateTime 2022/8/28 11:03
 */
@RestController
@RequestMapping("/orderDetail")
@Slf4j
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
        Page<OrdersDto> ordersDtoPage = orderDetailService.getPage(page, pageSize, number, beginTime, endTime);
        return R.success(ordersDtoPage);
    }

    @PutMapping
    public R<String> editOrderDetail(@RequestBody Orders orders) {
        orderService.updateById(orders);
        return R.success("操作成功");
    }
}
