package com.team.puddy.domain.question.controller;

import com.team.puddy.domain.ControllerTest;
import com.team.puddy.domain.TestEntityUtils;
import com.team.puddy.domain.question.QuestionFixture;
import com.team.puddy.domain.question.dto.request.QuestionServiceRegister;
import com.team.puddy.domain.question.dto.request.RequestQuestionDto;
import com.team.puddy.domain.question.dto.request.UpdateQuestionDto;
import com.team.puddy.domain.question.dto.response.QuestionListResponseDto;
import com.team.puddy.domain.question.dto.response.QuestionResponseDto;
import com.team.puddy.global.config.WithMockAuthUser;
import com.team.puddy.global.config.security.SecurityConfig;
import com.team.puddy.global.config.security.jwt.JwtAuthorizationFilter;
import com.team.puddy.global.config.security.jwt.JwtVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = QuestionController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtVerifier.class)
        })
@DisplayName("질문글 API 테스트")

public class QuestionControllerTest extends ControllerTest {

    @DisplayName("질문글 작성 API")
    @Test
    @WithMockAuthUser
    void givenRequest_whenAddQuestion_then200() throws Exception {
        // given
        Long userId = 1L;
        RequestQuestionDto requestQuestionDto = TestEntityUtils.requestQuestionDto();
        QuestionServiceRegister request = QuestionFixture.questionServiceRegister();

        MockMultipartFile image = TestEntityUtils.mockMultipartFile();

        when(questionMapper.toServiceDto(requestQuestionDto)).thenReturn(request);
        doNothing().when(questionService).addQuestion(request, List.of(image), userId);

        MockPart requestPart = new MockPart("request", objectMapper.writeValueAsString(requestQuestionDto).getBytes());
        requestPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        // when & then
        mockMvc.perform(multipart("/questions")
                        .file(image)
                        .part(requestPart)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"));

        verify(questionService, times(1)).addQuestion(request, List.of(image), userId);
    }

    @DisplayName("질문글 수정 API")
    @Test
    @WithMockAuthUser
    void givenUpdate_whenUpdateQuestion_then200() throws Exception {
        //given
        UpdateQuestionDto updateQuestionDto = TestEntityUtils.updateQuestionDto();
        Long questionId = 1L;
        MockMultipartFile image = TestEntityUtils.mockMultipartFile();
        MockPart updatePart = new MockPart("request", objectMapper.writeValueAsString(updateQuestionDto).getBytes());
        updatePart.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        //when & then
        mockMvc.perform(multipart("/questions/{questionId}", questionId)
                        .file(image)
                        .part(updatePart)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"));

        verify(questionService, times(1)).updateQuestion(questionId, updateQuestionDto, List.of(image), 1L);
    }

    @DisplayName("질문글 삭제 API")
    @Test
    @WithMockAuthUser
    void givenQuestionId_whenDeleteQuestion_then200() throws Exception {
        //given
        Long questionId = 1L;
        doNothing().when(questionService).deleteQuestion(questionId, 1L);

        //when & then
        mockMvc.perform(delete("/questions/{questionId}", questionId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"));

        verify(questionService, times(1)).deleteQuestion(questionId, 1L);
    }

    @DisplayName("질문글 조회 API 테스트")
    @Test
    @WithMockUser(username = "testUser", password = "password")
    void givenQuestionId_whenGetQuestion_then200() throws Exception {
        //given
        Long questionId = 1L;
        QuestionResponseDto questionResponseDto = TestEntityUtils.questionResponseDto();

        //when
        when(questionService.getQuestion(questionId)).thenReturn(questionResponseDto);
        doNothing().when(questionService).increaseViewCount(questionId);

        //then
        mockMvc.perform(get("/questions/{questionId}", questionId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.data.questionId").value(questionResponseDto.questionId()))
                .andExpect(jsonPath("$.data.title").value(questionResponseDto.title()))
                .andExpect(jsonPath("$.data.content").value(questionResponseDto.content()))
                .andExpect(jsonPath("$.data.category").value(questionResponseDto.category()))
                .andExpect(jsonPath("$.data.nickname").value(questionResponseDto.nickname()))
                .andExpect(jsonPath("$.data.pet").isNotEmpty())
                .andExpect(jsonPath("$.data.images").isEmpty())
                .andExpect(jsonPath("$.data.isSolved").value(questionResponseDto.isSolved()))
                .andExpect(jsonPath("$.data.postCategory").value(questionResponseDto.postCategory()))
                .andExpect(jsonPath("$.data.viewCount").value(questionResponseDto.viewCount()));

        verify(questionService, times(1)).increaseViewCount(questionId);
        verify(questionService, times(1)).getQuestion(questionId);
    }

    @DisplayName("질문글 리스트 조회 API 테스트")
    @Test
    @WithMockAuthUser
    void givenKeywordAndPage_whenGetQuestionList_then200() throws Exception {
        // given
        String keyword = "test";
        String sort = "desc";
        int page = 1;
        QuestionListResponseDto questionListResponseDto = TestEntityUtils.questionListResponseDto();

        // when
        when(questionService.getQuestionListByTitleStartWith(any(Pageable.class), eq(keyword),eq(sort)))
                .thenReturn(questionListResponseDto);

        // then
        mockMvc.perform(get("/questions")
                        .with(csrf())
                        .param("keyword", keyword)
                        .param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.data.questionList", hasSize(questionListResponseDto.questionList().size())));

        verify(questionService, times(1)).getQuestionListByTitleStartWith(any(Pageable.class), eq(keyword),eq(sort));
    }




}
