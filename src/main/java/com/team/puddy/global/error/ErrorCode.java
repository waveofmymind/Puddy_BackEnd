package com.team.puddy.global.error;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // Common
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "Entity Not Found"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "Invalid Type Value"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "Invalid Input Value"),
    METHOD_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "Invalid Method"),
    JSON_WRITE_ERROR(HttpStatus.BAD_REQUEST, "Json Write Error"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),
    SERVLET_ERROR(HttpStatus.BAD_REQUEST, "Servlet Error"),
    INVALID_ERROR(HttpStatus.BAD_REQUEST, "Invalid Error"),
    IMAGE_PROCESSING_ERROR(HttpStatus.BAD_REQUEST,"이미지 처리중 오류가 발생했습니다."),


    // JWT
    JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "Jwt Exception"),
    NOT_VALID_USER(HttpStatus.BAD_REQUEST, "권한이 없습니다."),

    TOKEN_VERIFY_FAIL(HttpStatus.BAD_REQUEST, "토큰 검증에 실패했습니다."),

    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 토큰입니다"),

    //USER
    DUPLICATE_ACCOUNT(HttpStatus.BAD_REQUEST, "중복된 아이디입니다"),
    DUPLICATE_USER(HttpStatus.BAD_REQUEST, "중복된 유저입니다"),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일입니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "가입된 유저가 아닙니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "유효하지 않은 패스워드입니다."),
    IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "업데이트할 이미지가 없습니다."),
    NOT_MATCH_INFO(HttpStatus.NOT_FOUND,"입력하신 개인정보가 일치하지 않습니다."),
    UNAUTHORIZED_OPERATION(HttpStatus.UNAUTHORIZED, "수정,삭제 권한이 없습니다."),
    SEND_EMAIL_FAIL(HttpStatus.BAD_REQUEST,"메일 전송에 실패했습니다."),
    CHANGE_PASSWORD_FAIL(HttpStatus.BAD_REQUEST,"패스워드 변경에 실패했습니다."),
    NEED_MORE_INFO(HttpStatus.BAD_REQUEST, "추가 정보가 필요합니다."),
    PUDDY_USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST,"잘못된 접근입니다."),
    PROVIDER_NOT_SUPPORTED(HttpStatus.BAD_REQUEST,"지원하지 않는 로그인 방식입니다."),
    INVALID_ACCOUNT(HttpStatus.BAD_REQUEST, "유효하지 않은 계정입니다."),



    //QUESTION
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 문제입니다."),
    NOT_THE_WRITER(HttpStatus.FORBIDDEN, "작성자만 채택할 수 있습니다."),

    //EXPERT
    EXPERT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 전문가입니다."),
    NOT_THE_EXPERT(HttpStatus.BAD_REQUEST,"해당 유저는 전문가가 아닙니다."),
    DUPLICATE_EXPERT(HttpStatus.BAD_REQUEST, "이미 등록된 전문가 유저입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    PET_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 유저는 펫을 가지지 않았습니다"),

    //ARTICLE
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "작성자만 답변글을 변경할 수 있습니다"),
    ARTICLE_LIKE_ERROR(HttpStatus.BAD_REQUEST, "게시글 좋아요 에러입니다."),
    //COMMENT
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),
    COMMENT_DELETE_ERROR(HttpStatus.BAD_REQUEST, "댓글 삭제 에러입니다."),
    MODIFIY_COMMENT_ERROR(HttpStatus.BAD_REQUEST, "댓글 수정 에러입니다.");


    private final HttpStatus status;
    private final String message;


}