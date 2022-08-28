package com.huweiv.config;

import com.huweiv.common.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @title 设置静态资源映射
 * @description 
 * @author HUWEIV
 * @date 2022/7/13 20:52
 * @return 
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * @title addResourceHandlers
     * @description 设置静态资源映射
     * @author HUWEIV
     * @date 2022/7/15 21:49
     * @return void
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * @title extendMessageConverters
     * @description 扩展mvc框架的消息转换器
     * @author HUWEIV
     * @date 2022/7/15 21:50
     * @return void
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层采用Jackson讲Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器对象集合中
        converters.add(0, messageConverter);
    }
}
