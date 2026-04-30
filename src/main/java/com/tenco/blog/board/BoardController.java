package com.tenco.blog.board;

import com.tenco.blog._core.errors.*;
import com.tenco.blog.user.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller // IoC
@RequiredArgsConstructor // DI
public class BoardController {
    // DI 처리해줘야 빨간줄 안나옴
    private final BoardPersistRepository boardPersistRepository;

    /**
     * 게시글 작성 화면 요청
     *
     * @return 페이지 반환
     * 주소설계: http://localhost:8080/board/save-form
     */
    @GetMapping("/board/save-form")
    public String saveForm(HttpSession httpSession) {
        // 1. 인증검사는 LoginInterceptor에서 먼저 처리함
        return "board/save-form";
    }

    /**
     * 게시글 작성 기능 요청
     *
     * @return 페이지 반환
     * 주소설계: http://localhost:8080/board/save-form
     */

    @PostMapping("/board/save")
    // 사용자요청 >> HTTP요청 메시지(Post)
    public String saveProc(BoardRequest.SaveDTO saveDTO, HttpSession session) {

        log.info("=== 게시글 저장 요청 ===");
        // 이 요청시 사용자가 로그인을 했다면 로그인 정보를 세션 메모리에서 가져오면 된다.
        // 1. 세션에서 로그인한 사용자 정보 가져오기
        User sessionUser = (User) session.getAttribute("sessionUser");

        try {
            // 3. 로그인 된 사용자
            // 3.1 유효성 검사
            saveDTO.validate();

            Board board = saveDTO.toEntity(sessionUser);
            boardPersistRepository.save(board);

            return "redirect:/";
        } catch (Exception e) {
            System.out.println("에러발생 : " + e.getMessage());
            return "board/save-form";
        }
    }

    /**
     * 게시글 목록 화면 요청
     * * 주소설계: http://localhost:8080/
     *
     * @return
     */
    @GetMapping({"/", "index"})
    public String list(Model model) {
        List<Board> boardList = boardPersistRepository.findAll();
        model.addAttribute("boardList", boardList);
        return "board/list";
    }

    // 게시글 상세보기 화면 요청 처리
    // http://localhost:8080/board/1
    @GetMapping("/board/{id}")
    public String detailPage(@PathVariable(name = "id") Integer id, Model model) {
        // 유효성 검사, 인증 검사
        Board board = boardPersistRepository.findById(id);
        model.addAttribute("board", board);

        return "board/detail";
    }

    // 게시글 삭제하기
    // 1. 로그인 여부 확인
    // 2. 삭제할 게시글이 본인이 작성한 게시글인지 확인 (권한 확인, 인가 처리)
    // 3. 인가처리 후 삭제 진행
    // /board{{board.id}}/delete
    @PostMapping("/board/{id}/delete")
    public String deleteProc(@PathVariable(name = "id") Integer id, HttpSession session) {
        log.info("--- 게시글 삭제 요청 ---");
        // 인증 검사
        User sessionUser = (User) session.getAttribute("sessionUser");

        try {
            // 삭제할 게시글 조회 (권한체크, 인가처리)
            Board board = boardPersistRepository.findById(id);
            if (board.getUser().getId() == sessionUser.getId()) {
                boardPersistRepository.deleteById(id);
            } else {
                throw new Exception403("삭제 권한이 없습니다.");
            }
        } catch (Exception e) {
            throw new Exception403("삭제 권한이 없습니다.");
        }

        // PRG패턴 (Post >> Redirect >> Get) 적용
        return "redirect:/";
    }

    // 게시글 수정화면 요청
    // http://localhost:8080/board/1/update-form
    @GetMapping("/board/{id}/update-form")
    public String updateFormPage(@PathVariable(name = "id") Integer id, Model model, HttpSession session) {
        // 인증 처리
        User sessionUser = (User) session.getAttribute("sessionUser");

        // 인가처리
        // 사용자에게 해당 게시물 내용을 보여줘야 한다.
        // 조회 기능 - 게시글 id로
        Board board = boardPersistRepository.findById(id);
        if (sessionUser.getId() != board.getUser().getId()) {
            throw new Exception403("수정 권한이 없습니다.");
        }
        model.addAttribute("board", board);
        return "board/update-form";
    }

    // /board/{id}/update
    @PostMapping("/board/{id}/update")
    // 메세지 컨버터란 객체가 동작해서 자동으로 객체를 생성하고 값을 매핑해준다.
    public String updateProc(@PathVariable(name = "id") Integer id,
                             BoardRequest.UpdateDTO updateDTO, HttpSession session) {
        // 인증검사
        User sessionUser = (User) session.getAttribute("sessionUser");

        // 유효성 검사
        try {
            updateDTO.validate();
            // 인가검사
            Board board = boardPersistRepository.findById(id);
            if (sessionUser.getId() != board.getUser().getId()) {
                throw new RuntimeException("수정할 권한이 없습니다.");
            }
            boardPersistRepository.updateById(id, updateDTO);
        } catch (Exception e) {
            return "redirect:/board/" + id + "/update-form";
        }
        return "redirect:/board/" + id;
    }
}
