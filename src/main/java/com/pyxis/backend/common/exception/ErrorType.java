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
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, " 접근 권한이 없습니다."),
    BOTCHAT_NOT_FOUND(HttpStatus.BAD_REQUEST, "채팅방이 존재하지 않습니다."),
    COMM_POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "커뮤니티 게시글이 존재하지 않습니다."),

    COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "댓글이 존재하지 않습니다."),

    POST_TYPE_ERROR(HttpStatus.BAD_REQUEST, "게시글 타입이 맞지 않습니다."),
    GENDER_TYPE_ERROR(HttpStatus.BAD_REQUEST, "성별 타입이 맞지 않습니다."),
    AI_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI 서비스와 통신 중 오류가 발생했습니다."),
    INVALID_PARENT_COMMENT(HttpStatus.BAD_REQUEST, "부모 댓글이 현재 게시글에 속해 있지 않습니다");

    private final HttpStatus status;
    private final String description;
}
