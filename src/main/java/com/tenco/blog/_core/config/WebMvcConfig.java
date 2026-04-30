package com.tenco.blog._core.config;

import com.tenco.blog._core.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 자바코드로 스프링부트 설정 파일을 다룰 수 있다.
// @Component
@RequiredArgsConstructor // DI
@Configuration // IoC 대상 - 하나 이상의 IoC 처리를 하고 싶을 때 사용한다.
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired // DI 처리
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 여기에 LoginInterceptor 등록할 예정
        System.out.println("인터셉터 동작함");
        registry.addInterceptor(new LoginInterceptor())
                // 이 loginInterceptor가 동작할 URL 패턴을 명시해줘야한다.
                .addPathPatterns("/board/**", "/user/**")
                // 인터셉터에서 제외할 URL 패턴을 지정
                // /board/7 << 정수값이 들어오면 제외시켜라
                .excludePathPatterns("/board/{id:\\d+}");
        // 예: board/1, board/7 등은 로그인 없어도 접근 가능
    }
}
