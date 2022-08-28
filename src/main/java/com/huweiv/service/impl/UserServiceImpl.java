package com.huweiv.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huweiv.entity.User;
import com.huweiv.mapper.UserMapper;
import com.huweiv.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName UserServiceImpl
 * @Description TODO
 * @CreateTime 2022/8/27 19:06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
