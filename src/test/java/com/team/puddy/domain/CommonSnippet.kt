package com.team.puddy.domain

import org.assertj.core.util.Lists
import org.springframework.restdocs.constraints.Constraint
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.snippet.Attributes
import org.springframework.restdocs.snippet.Snippet
import java.util.*


class CommonSnippet {

    companion object {

        val accessTokenHeader: Snippet = requestHeaders(
                headerWithName("Authorization").attributes(required()).description(" access token")
        )

        fun responseField(): Snippet = requestFields(
                fieldWithPath("resultCode").description("응답 메시지"),
                fieldWithPath("data").description("데이터"),
        )

        fun required(): Attributes.Attribute? {
            val constraints: List<Constraint> = Lists.newArrayList(Constraint("javax.validation.constraints.NotNull", Collections.emptyMap()))
            return Attributes.key("validationConstraints").value(constraints)
        }
    }
}