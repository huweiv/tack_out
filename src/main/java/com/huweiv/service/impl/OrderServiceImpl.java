package com.huweiv.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huweiv.common.BaseContext;
import com.huweiv.dto.OrdersDto;
import com.huweiv.entity.*;
import com.huweiv.exception.BusinessException;
import com.huweiv.mapper.AddressBookMapper;
import com.huweiv.mapper.OrderMapper;
import com.huweiv.mapper.UserMapper;
import com.huweiv.service.OrderDetailService;
import com.huweiv.service.OrderService;
import com.huweiv.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName OrderServiceImpl
 * @Description TODO
 * @CreateTime 2022/8/28 11:01
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailService orderDetailService;

    @Transactional
    @Override
    public void submit(Orders orders) {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        if (shoppingCartList == null || shoppingCartList.size() == 0)
            throw  new BusinessException("购物车为空");
        User user = userMapper.selectById(currentId);
        AddressBook addressBook = addressBookMapper.selectById(orders.getAddressBookId());
        if (addressBook == null)
            throw new BusinessException("地址信息有误");
        //订单号，调用mybatis-plus提供的类
        long orderId = IdWorker.getId();
        //计算金额，AtomicInteger是多线程安全的
        AtomicInteger amount = new AtomicInteger();
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        orderMapper.insert(orders);
        orderDetailService.saveBatch(orderDetailList);
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }

    @Override
    public Page<OrdersDto> getPage(int page, int pageSize) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getUserId, BaseContext.getCurrentId());
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

    @Override
    public void again(Orders orders) {
        LambdaQueryWrapper<OrderDetail> lqw = new LambdaQueryWrapper<>();
        lqw.eq(OrderDetail::getOrderId, orders.getId());
        List<OrderDetail> orderDetailList = orderDetailService.list(lqw);
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((item) -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            if (item.getDishId() != null)
                shoppingCart.setDishId(item.getDishId());
            if (item.getSetmealId() != null)
                shoppingCart.setSetmealId(item.getSetmealId());
            shoppingCart.setName(item.getName());
            shoppingCart.setImage(item.getImage());
            shoppingCart.setUserId(currentId);
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());
        log.info("shop{}", shoppingCartList.toString());
        shoppingCartService.saveBatch(shoppingCartList);
    }
}
