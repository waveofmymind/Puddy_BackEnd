package com.team.puddy.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.puddy.domain.answer.controller.AnswerController;
import com.team.puddy.domain.article.controller.ArticleController;
import com.team.puddy.domain.comment.controller.CommentController;
import com.team.puddy.domain.expert.controller.ExpertController;
import com.team.puddy.domain.pet.controller.PetController;
import com.team.puddy.domain.question.controller.QuestionController;
import com.team.puddy.domain.question.service.QuestionService;
import com.team.puddy.domain.user.controller.UserController;
import com.team.puddy.domain.user.service.UserService;
import com.team.puddy.global.config.security.SecurityConfig;
import com.team.puddy.global.config.security.jwt.JwtAuthorizationFilter;
import com.team.puddy.global.config.security.jwt.JwtVerifier;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {UserController.class,
        QuestionController.class,
//        AnswerController.class,
//        ArticleController.class,
//        CommentController.class,
//        HomeController.class,
//        PetController.class,
//        ExpertController.class
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtVerifier.class)
        })
public abstract class ControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public ObjectMapper objectMapper;

    @MockBean
    public UserService userService;

    @MockBean
    public QuestionService questionService;

}
