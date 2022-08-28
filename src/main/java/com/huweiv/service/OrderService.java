package com.huweiv.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huweiv.dto.OrdersDto;
import com.huweiv.entity.Orders;

public interface OrderService extends IService<Orders> {
    public void submit(Orders orders);
    public Page<OrdersDto> getPage(int page, int pageSize);
}
