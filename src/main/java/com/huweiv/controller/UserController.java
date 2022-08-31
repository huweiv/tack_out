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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request) {
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}", code);
            //利用阿里云发送短信验证码
//            SMSUtils.sendMessage("", "", phone, code);
            //生成的验证码缓存到session中
//            request.getSession().setAttribute(phone, code);
            //生成的验证码缓存到redis中，设置有效时间为5分钟
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            return R.success("发送成功");
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpServletRequest request) {
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
//        String sendCode = (String) request.getSession().getAttribute(phone);
        String sendCode = (String) redisTemplate.opsForValue().get(phone);
        if (StringUtils.isNotEmpty(sendCode) && sendCode.equals(code)) {
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone, phone);
            User user = userService.getOne(lqw);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                user.setName("用户" + phone.substring(0, 3) + phone.substring(7));
                userService.save(user);
            }
            request.getSession().setAttribute("user", user.getId());
            redisTemplate.delete(phone);
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
