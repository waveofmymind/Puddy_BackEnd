package com.team.puddy.domain;

import org.springframework.restdocs.snippet.Snippet;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

public class CommonSnippet {

    public static final Snippet accessTokenHeader = requestHeaders(
            headerWithName("Authorization").description(" access token").getAttributes()
    );

    public static final Snippet responseField = requestFields(
            fieldWithPath("resultCode").description("응답 메시지"),
            fieldWithPath("data").description("데이터")
    );
}
