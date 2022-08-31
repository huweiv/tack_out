package com.huweiv.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huweiv.dto.OrdersDto;
import com.huweiv.entity.OrderDetail;
import com.huweiv.entity.Orders;
import com.huweiv.mapper.OrderDetailMapper;
import com.huweiv.mapper.OrderMapper;
import com.huweiv.service.OrderDetailService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName OrderDetailServiceImpl
 * @Description TODO
 * @CreateTime 2022/8/28 11:02
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailService orderDetailService;


    @Override
    public Page<OrdersDto> getPage(int page, int pageSize, String number, String beginTime, String endTime) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(number), Orders::getNumber, number);
        lqw.between(StringUtils.isNotEmpty(beginTime), Orders::getOrderTime, beginTime, endTime);
        lqw.orderByDesc(Orders::getOrderTime);
        orderMapper.selectPage(ordersPage, lqw);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");
        List<Orders> ordersList = ordersPage.getRecords();
        List<OrdersDto> ordersDtoList = ordersList.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailLambdaQueryWrapper);
            ordersDto.setOrderDetails(orderDetailList);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(ordersDtoList);
        return ordersDtoPage;
    }
}
