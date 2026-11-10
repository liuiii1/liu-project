package com.cl.config;

import com.cl.interceptor.AuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Value("${app.upload-path:d:/2026-competition/NEW/code/后端/micro-cloud/upload/file/}")
    private String uploadPath;

    @Bean
    public AuthorizationInterceptor getAuthorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getAuthorizationInterceptor()).addPathPatterns("/**").excludePathPatterns("/static/**");
    }

    /**
     * 静态资源映射：
     * 1. classpath 里的静态资源（种子数据图片等）
     * 2. /file/** → upload-path（用户上传的文件，从配置文件读取，避免 user.dir 不一致问题）
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/")
                .addResourceLocations("classpath:/public/");

        // file: 前缀指向磁盘绝对路径，末尾必须带 /
        String location = uploadPath;
        if (!location.startsWith("file:")) {
            location = "file:" + location;
        }
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler("/file/**")
                .addResourceLocations(location);
    }
}
