package com.team.puddy.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.puddy.domain.ControllerTest;
import com.team.puddy.domain.TestEntityUtils;
import com.team.puddy.domain.user.dto.request.LoginUserRequest;

import com.team.puddy.domain.user.dto.request.RegisterUserRequest;
import com.team.puddy.domain.user.service.UserService;

import com.team.puddy.global.config.security.jwt.LoginToken;

import com.team.puddy.global.error.ErrorCode;
import com.team.puddy.global.error.exception.BusinessException;
import com.team.puddy.global.error.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("유저 API 테스트")
public class UserControllerTest extends ControllerTest {


    @DisplayName("로그인 API 테스트")
    @Test
    @WithMockUser
    void givenRequest_whenLogin_then200() throws Exception {
        //given
        LoginUserRequest loginUserRequest = TestEntityUtils.loginUserRequest();
        LoginToken loginToken = TestEntityUtils.loginToken();
        //when
        when(userService.login(loginUserRequest)).thenReturn(loginToken);

        //then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(loginUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accessToken").value("Bearer sample-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("Bearer sample-refresh-token"));
    }

    @DisplayName("로그인시 아이디가 다를 경우 예외")
    @Test
    @WithMockUser
    void givenWrongAccount_whenLogin_then400() throws Exception {
        //given
        LoginUserRequest loginUserRequest = TestEntityUtils.loginUserRequest();

        //when
        when(userService.login(loginUserRequest)).thenThrow(new NotFoundException(ErrorCode.INVALID_ACCOUNT));

        //then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(loginUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("유효하지 않은 계정입니다."));

        verify(userService, times(1)).login(loginUserRequest);
    }

    @DisplayName("로그인시 비밀번호가 틀린 경우 예외")
    @Test
    @WithMockUser
    void givenWrongPassword_whenLogin_then() throws Exception {
        //given
        LoginUserRequest loginUserRequest = TestEntityUtils.loginUserRequest();

        //when
        when(userService.login(loginUserRequest)).thenThrow(new BusinessException(ErrorCode.INVALID_PASSWORD));

        //then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(loginUserRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("INVALID_PASSWORD"));

        verify(userService, times(1)).login(loginUserRequest);
    }

    @DisplayName("회원가입 API 테스트")
    @Test
    @WithMockUser
    void givenRequest_whenJoin_then201() throws Exception {
        //given
        RegisterUserRequest registerUserRequest = TestEntityUtils.registerUserRequest();
        //when
        doNothing().when(userService).join(registerUserRequest);
        //then
        mockMvc.perform(post("/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(userService, times(1)).join(registerUserRequest);

    }
}

