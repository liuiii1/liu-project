package com.cl.config;

import com.cl.interceptor.AuthorizationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

    @Bean
    public AuthorizationInterceptor getAuthorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getAuthorizationInterceptor()).addPathPatterns("/**").excludePathPatterns("/static/**");
        super.addInterceptors(registry);
    }

    /**
     * springboot 2.0配置WebMvcConfigurationSupport之后，会导致默认配置被覆盖，要访问静态资源需要重写addResourceHandlers方法
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // classpath 里的静态资源（种子数据图片、管理后台 build 产物等）
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/")
                .addResourceLocations("classpath:/public/");
        // 外部 upload/file/ 目录（用户上传的文件，不走 classpath，clean compile 不丢）
        String projectRoot = System.getProperty("user.dir");
        String uploadPath = "file:" + projectRoot.replace("\\", "/") + "/upload/file/";
        registry.addResourceHandler("/file/**")
                .addResourceLocations(uploadPath);
        super.addResourceHandlers(registry);
    }
}
