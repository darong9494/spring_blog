package com.tenco.blog.model;


import com.tenco.blog.util.MyDateUtil;
import jakarta.persistence.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;

@Data // get,set, toString ..
// @Entity - JPA가 이 클래스를 데이터베이스 테이블과 매핑하는 객체로 인식하게 설정
// 즉, 이 어노테이션이 있어야 JPA가 관리 함
@Entity
@Table(name = "board_tb")
public class Board {

    // @id : 이 필드가 기본키임을 설정 함
    @Id
    // IDENTITY 전략: 데이터베이스게 기본 AUTO_INCREMENT 기능 사용
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String title;
    private String content;
    private Timestamp createdAt;

    // createdAt >> 포멧하는 메소드 만들어보기
    public String getTime() {
        return MyDateUtil.timestampFormat(createdAt);
    }

//    // 테스트코드
//    public static void main(String[] args) {
//        boolean isBlank = StringUtils.isBlank("     ");
//        System.out.println(isBlank);
//
//        // 문자열 뒤집기
//        String reversed = StringUtils.reverse("tencoding");
//        System.out.println(reversed);
//    }
}

