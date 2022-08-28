package com.huweiv.filter;

import com.alibaba.fastjson.JSON;
import com.huweiv.common.BaseContext;
import com.huweiv.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author HUWEIV
 * @version 1.0.0
 * @ClassName LoginCheckFilter
 * @Description TODO
 * @CreateTime 2022/7/15 15:16
 */

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();
        //定义不需要处理的请求路径
        String[] urls = new String[] {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",  //移动端发送验证码
                "/user/login"     //移动端用户登录
        };
        //2.判断本次请求是否需要处理，如果不需要处理，直接放行
        if (check(urls, requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        //3-1.判断登录状态，已登录则放行
        Long empId = (Long) request.getSession().getAttribute("employee");
        if (empId != null) {
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }

        //3-2.判断移动端用户登录状态，已登录则放行
        Long userId = (Long) request.getSession().getAttribute("user");
        if (userId != null) {
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }

        //4.若未登录则拦截，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    /**
     * @title check
     * @description 检查本次请求是否需要放行
     * @author HUWEIV
     * @date 2022/7/15 15:27
     * @return boolean
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
