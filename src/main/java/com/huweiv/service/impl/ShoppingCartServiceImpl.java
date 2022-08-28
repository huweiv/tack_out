package com.huweiv.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huweiv.entity.ShoppingCart;
import com.huweiv.mapper.ShoppingCartMapper;
import com.huweiv.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName ShoppingCarServiceImpl
 * @Description TODO
 * @CreateTime 2022/8/28 9:08
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
