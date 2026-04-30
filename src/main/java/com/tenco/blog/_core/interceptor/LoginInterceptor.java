package com.tenco.blog._core.interceptor;

import com.tenco.blog._core.errors.Exception401;
import com.tenco.blog.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component // IoC 대상 - 싱글톤 패턴
public class LoginInterceptor implements HandlerInterceptor {
    // 컨트롤러에 들어오기 전에 먼저 동작함
    // return에 true가 있으면 Controller로 진행된다.
    // return에 false가 있으면 안들여보낸다. (Controller 진입 불가)
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 인증 검사 - 세션 >> 값있나없나
       HttpSession session = request.getSession();
      User sessionUser = (User) session.getAttribute("sessionUser");
      if (sessionUser == null) {
          throw new Exception401("로그인 먼저 해주세요");
      }
        return true;
    }

    // 뷰가 렌더링 되기 전에 낚아채는 녀석
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    // 요청처리가 완료된 후, 뷰가 완전 렌더링이 된 후 호출된다.
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
