package com.tenco.blog.reply;

import com.tenco.blog.user.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller // IoC
@RequiredArgsConstructor // DI처리 이걸로 쓰기
public class ReplyController {
    // @Autowired // DI 처리 이거말고 final써서 위에 Require 쓰기
    private final ReplyService replyService;

    // 댓글 등록 기능 요청
    @PostMapping("/reply/save")
    public String saveProc(ReplyRequest.SaveDTO saveDTO, HttpSession session) {
        // 1. 인증검사(로그인 여부 확인) >> LoginInterceptor 처리
        User sessionUser = (User) session.getAttribute("sessionUser");
        // 2. 유효성 검사 (comment 값 확인)
        saveDTO.validate();

        replyService.댓글작성(saveDTO, sessionUser.getId());
        // 해당 게시글에 댓글 작성 후 리다이렉션 처리 (해당 게시글로)
        return "redirect:/board/" + saveDTO.getBoardId();
    }
}
