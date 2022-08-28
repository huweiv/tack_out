package com.huweiv.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huweiv.common.R;
import com.huweiv.common.SMSUtils;
import com.huweiv.common.ValidateCodeUtils;
import com.huweiv.entity.User;
import com.huweiv.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName UserController
 * @Description TODO
 * @CreateTime 2022/8/27 19:07
 */

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request) {
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}", code);
//            SMSUtils.sendMessage("", "", phone, code);
            request.getSession().setAttribute(phone, code);
            return R.success("发送成功");
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpServletRequest request) {
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        String sendCode = (String) request.getSession().getAttribute(phone);
        if (sendCode != null && sendCode.equals(code)) {
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone, phone);
            User user = userService.getOne(lqw);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            request.getSession().setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
