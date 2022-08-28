package com.huweiv.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huweiv.entity.AddressBook;
import com.huweiv.mapper.AddressBookMapper;
import com.huweiv.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName AddressBookServiceImpl
 * @Description TODO
 * @CreateTime 2022/8/27 20:14
 */

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
