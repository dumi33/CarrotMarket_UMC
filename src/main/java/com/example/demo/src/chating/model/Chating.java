package com.example.demo.src.chating.model;


import lombok.*;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(userIdx, nickname, email, password)를 받는 생성자를 생성


public class Chating {
    private int chatIdx;
    private int roomIdx;
    private int sender;
    private String content;
}