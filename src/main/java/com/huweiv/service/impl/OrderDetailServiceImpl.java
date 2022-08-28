package com.huweiv.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huweiv.common.BaseContext;
import com.huweiv.dto.OrdersDto;
import com.huweiv.entity.AddressBook;
import com.huweiv.entity.OrderDetail;
import com.huweiv.entity.Orders;
import com.huweiv.entity.User;
import com.huweiv.mapper.AddressBookMapper;
import com.huweiv.mapper.OrderDetailMapper;
import com.huweiv.mapper.OrderMapper;
import com.huweiv.mapper.UserMapper;
import com.huweiv.service.OrderDetailService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private OrderDetailService orderDetailService;


    @Override
    public Page<OrdersDto> getPage(int page, int pageSize, String number, String beginTime, String endTime) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotEmpty(number), Orders::getNumber, number);
        lqw.ge(StringUtils.isNotEmpty(beginTime), Orders::getOrderTime, beginTime);
        lqw.lt(StringUtils.isNotEmpty(endTime), Orders::getOrderTime, endTime);
        lqw.orderByDesc(Orders::getOrderTime);
        orderMapper.selectPage(ordersPage, lqw);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");
        List<Orders> ordersList = ordersPage.getRecords();
        List<OrdersDto> ordersDtoList = ordersList.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            AddressBook addressBook = addressBookMapper.selectById(item.getAddressBookId());
            ordersDto.setConsignee(addressBook.getConsignee());
            ordersDto.setPhone(addressBook.getPhone());
            ordersDto.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                    + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                    + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                    + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
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
