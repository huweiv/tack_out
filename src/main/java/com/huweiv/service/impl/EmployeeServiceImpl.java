package com.huweiv.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huweiv.entity.Employee;
import com.huweiv.mapper.EmployeeMapper;
import com.huweiv.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName EmployeeServiceImpl
 * @Description TODO
 * @CreateTime 2022/7/13 21:26
 */

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
