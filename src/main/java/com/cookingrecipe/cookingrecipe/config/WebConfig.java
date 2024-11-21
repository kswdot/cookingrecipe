package com.cookingrecipe.cookingrecipe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// 저장된 이미지를 웹에서 볼 수 있도록 정적 리소스 핸들러 추가
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/Users/user/.gradle/cookingrecipe/uploaded-images/");
                // 실제 파일 경로
                // 해당 경로로 업로드한 파일이 저장됨
    }
}
