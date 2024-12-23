package com.cookingrecipe.cookingrecipe;

import com.cookingrecipe.cookingrecipe.config.FileConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
public class CookingrecipeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CookingrecipeApplication.class, args);
		FileConfig.createUploadDir(); // 디렉토리 확인 및 생성

		// 애플리케이션 실행 디렉토리 출력
		System.out.println("애플리케이션 실행 디렉토리: " + System.getProperty("user.dir"));
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.of(UUID.randomUUID().toString());
	}

}
