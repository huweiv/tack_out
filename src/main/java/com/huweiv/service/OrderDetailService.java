package com.huweiv.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huweiv.dto.OrdersDto;
import com.huweiv.entity.OrderDetail;

public interface OrderDetailService extends IService<OrderDetail> {

    public Page<OrdersDto> getPage(int page, int pageSize, String number, String beginTime, String endTime);
}
