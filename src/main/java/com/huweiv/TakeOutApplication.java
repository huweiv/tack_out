package com.huweiv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching //开始缓存注解功能
public class TakeOutApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakeOutApplication.class, args);
		log.info("take_out success running");
	}

}
