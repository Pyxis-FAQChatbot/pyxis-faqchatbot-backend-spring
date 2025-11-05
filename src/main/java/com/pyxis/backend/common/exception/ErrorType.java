package com.pyxis.backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor

public enum ErrorType {

    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임이 있습니다."),
    LOGINID_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디가 있습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "입력한 비밀번호가 일치하지 않습니다." ),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

    BOTCHAT_NOT_FOUND(HttpStatus.BAD_REQUEST, "채팅방이 존재하지 않습니다.");


    private final HttpStatus status;
    private final String description;
}
