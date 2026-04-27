package com.tenco.blog.board;

import lombok.Builder;
import lombok.Data;

// 요청 데이터를 담는 DTO 클래스
// 컨트롤러, 비지니스, 데이터 계층 사이에서 데이터 전송 역할객체
public class BoardRequest {
    @Data
    @Builder
    public static class SaveDTO {
        private String username;
        private String title;
        private String content;

        // 편의기능 설계 가능
        // DTO에서 Entity로 변환해주는 편의 메소드
        public Board toEntity() {
            return Board.builder()
                    .username(username)
                    .title(title)
                    .content(content)
                    .build();
        }
    }
}
