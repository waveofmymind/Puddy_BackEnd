package com.team.puddy.domain.question;

import com.team.puddy.domain.question.dto.request.QuestionServiceRegister;

public class QuestionFixture {

    public static QuestionServiceRegister questionServiceRegister() {
        return QuestionServiceRegister.builder()
                .title("title")
                .content("content")
                .category("category")
                .postCategory(1)
                .build();
    }
}
