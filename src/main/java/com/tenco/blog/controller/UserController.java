package com.tenco.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    // 회원가입 페이지 요청
    // 주소설계 : http://localhost:8080/join-form
    @GetMapping("/join-form")
    public String joinForm(){
        // 뷰 리절브 동작
        // classPath: src/main/resources/templates/ 기본이 여기까지임
        return "user/join-form";
    }
}
