package com.huweiv.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huweiv.common.R;
import com.huweiv.entity.Employee;
import com.huweiv.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName EmployeeController
 * @Description TODO
 * @CreateTime 2022/7/13 21:30
 */

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * @title login
     * @description 登录
     * @author HUWEIV
     * @date 2022/7/15 16:10
     * @return com.huweiv.common.R<com.huweiv.entity.Employee>
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<Employee>();
        lqw.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(lqw);
        if (emp == null)
            return R.error("用户不存在");
        if (!emp.getPassword().equals(password))
            return R.error("用户密码错误");
        if (emp.getStatus() == 0)
            return R.error("用户账号禁用");
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * @title logout
     * @description 退出登录
     * @author HUWEIV
     * @date 2022/7/15 16:09
     * @return com.huweiv.common.R<java.lang.String>
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * @title save
     * @description 新增员工
     * @author HUWEIV
     * @date 2022/7/15 16:09
     * @return com.huweiv.common.R<java.lang.String>
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee) {

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增成功");
    }

    /**
     * @title page
     * @description 员工信息查询
     * @author HUWEIV
     * @date 2022/7/15 20:01
     * @return com.huweiv.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<Employee>();
        lqw.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        lqw.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, lqw);
        return R.success(pageInfo);
    }

    @GetMapping("/{id}")
    public R<Employee> edit(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null)
            return R.success(employee);
        else
            return R.error("没有该用户");
    }

    @PutMapping
    public R<String> update(@RequestBody Employee employee) {
//        employee.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("修改成功");

    }


}
