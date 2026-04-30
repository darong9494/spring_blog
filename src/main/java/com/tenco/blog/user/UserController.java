package com.tenco.blog.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller // IoC
@Slf4j
@RequiredArgsConstructor // DI 처리
public class UserController {
    private final HttpSession httpSession;
    private final UserRepository userRepository;

    // 프로필 수정 기능 요청
    // /user/update-form
    @PostMapping("/user/update")
    public String updateProc(UserRequest.UpdateDTO updateDTO, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");

        try {
            updateDTO.validate();
            // 영속성 컨텍스트
            // 더티체킹 전략
            User userEntity = userRepository.updateById(sessionUser.getId(), updateDTO);
            // 세션 동기화 처리
            session.setAttribute("sessionUser", userEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/";
    }

    @GetMapping("/user/update-form")
    public String updateFormPage(HttpSession session, Model model) {
        // 인증검사
        User sessionUser = (User) session.getAttribute("sessionUser");

        User userEntity = userRepository.findById(sessionUser.getId());
        userEntity.setPassword("");
        // 가방에 데이터 담아서 화면에 값 내려주기
        model.addAttribute("user", userEntity);
        return "user/update-form";
    }

    // 로그인 화면 요청
    // 주소 설계 : http://localhost:8080/login-form
    // /user/** 아님
    @GetMapping("/login-form")
    public String loginFormPage() {
        return "user/login-form";
    }

    // 로그인 기능 요청
    @PostMapping("/login")
    public String loginProc(UserRequest.LoginDTO loginDTO) {
        // 유효성 검사
        loginDTO.validate();

        User sessionUser = userRepository
                .findByUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword());
        if (sessionUser == null) {
            // 로그인 실패 (username, password) 불일치
            throw new IllegalArgumentException("사용자명 또는 비밀번호가 잘못 되었습니다.");
        }

        // 여기에 코드가 도달한다면 우리 DB에 정상 사용자임을 논리적으로 확인된다.
        httpSession.setAttribute("sessionUser", sessionUser);

        return "redirect:/";
    }

    // 로그아웃 기능 요청
    // 주소 설계 : http://localhost:8080/
    @GetMapping("/logout")
    public String logout() {
        // 세션 메모리에 내 정보를 없애는 것이다.
        // 로그아웃 기능
        httpSession.invalidate();
        return "redirect:/";
    }

    // 회원가입 화면 요청
    // 주소 설계 : http://localhost:8080/join-form
    @GetMapping("/join-form")
    public String joinFormPage() {
        return "user/join-form";
    }

    // 회원가입 기능 요청
    // 주소 설계 : http://localhost:8080/join/{id}
    //    public String joinProc(@RequestParam(name = "username") String username,
//                           @RequestParam(name = "password") String password,
//                           @RequestParam(name = "email") String email) {

    // 메세지 컨버터라는 녀석이 구문을 분석해서 자동으로 파싱처리 및 매핑해준다.
    // 파싱전략 1 - key=value 구조 (@RequestParam 사용)
    // 파싱전략 2 - Object DTO 설계 (UserRequest 클래스만들기)
    @PostMapping("/join")
    public String joinProc(UserRequest.JoinDTO joinDTO) {
        // 사용자가 던진 값을 기본 파싱 전략으로 받아서 콘솔 화면에 출력

        // 1. 유효성 검사하기
        joinDTO.validate(); // 오류 >> 예외처리로 넘어감

        // 회원가입 요청전 >> 중복 username 검사
        User userCheckName = userRepository.findByUsername(joinDTO.getUsername());

        if (userCheckName != null) {
            throw new IllegalArgumentException("이미 사용중인 username 입니다." + userCheckName.getUsername());
        }
        // User user = joinDTO.toEntity();
        userRepository.save(joinDTO.toEntity());

        return "redirect:/login-form";
    }
}
