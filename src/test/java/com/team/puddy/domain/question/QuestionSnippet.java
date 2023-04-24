package com.team.puddy.domain.question;

import org.springframework.restdocs.snippet.Snippet;

import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;

public class QuestionSnippet {

    public static final Snippet requestQuestionField = requestParts(
            partWithName("title").description("질문 제목"),
            partWithName("content").description("질문 내용"),
            partWithName("category").description("카테고리"),
            partWithName("images").description("이미지"),
            partWithName("postCategory").description("글 분류")
        );
}
