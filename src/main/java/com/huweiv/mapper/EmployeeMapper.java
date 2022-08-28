package com.huweiv.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huweiv.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
