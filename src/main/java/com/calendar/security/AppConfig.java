package com.calendar.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by NAVER on 2017-08-11.
 */
@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {

    @Autowired
    MyInterceptor requestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("app");
        registry.addInterceptor(requestInterceptor)
                .addPathPatterns("/tempPdf/**");
    }

}
