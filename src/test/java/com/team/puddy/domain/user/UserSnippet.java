package com.team.puddy.domain.user;

import org.assertj.core.util.Lists;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.restdocs.snippet.Snippet;


import java.util.Collections;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

public class UserSnippet {

    public static final Snippet AccessTokenHeader = requestHeaders(
            headerWithName("Authorization").attributes(required()).description("access token").getAttributes()
    );

    public static Attributes.Attribute required() {
        List<Constraint> constraints = Lists.newArrayList(new Constraint("javax.validation.constraints.NotNull", Collections.emptyMap()));
        return Attributes.key("validationConstraints").value(constraints);
    }
}
