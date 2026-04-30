package com.tenco.blog._core.errors;

public class Exception500 extends RuntimeException{
    public  Exception500(String msg) {
        super(msg); // 즉, 부모 클래스 메세지도 내가 직접 작성 부분으로 설정됨.
    }
}
