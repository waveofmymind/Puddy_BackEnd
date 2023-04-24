package com.team.puddy.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.puddy.domain.ControllerTest;
import com.team.puddy.domain.TestEntityUtils;
import com.team.puddy.domain.user.dto.request.LoginUserRequest;

import com.team.puddy.domain.user.dto.request.RegisterUserRequest;
import com.team.puddy.domain.user.dto.response.ResponsePostDto;
import com.team.puddy.domain.user.dto.response.ResponseUserInfoDto;
import com.team.puddy.domain.user.dto.response.TokenReissueDto;
import com.team.puddy.domain.user.service.UserService;

import com.team.puddy.global.common.dto.Response;
import com.team.puddy.global.config.WithMockAuthUser;
import com.team.puddy.global.config.auth.JwtUserDetails;
import com.team.puddy.global.config.security.jwt.LoginToken;

import com.team.puddy.global.error.ErrorCode;
import com.team.puddy.global.error.exception.BusinessException;
import com.team.puddy.global.error.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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


    @DisplayName("토큰 재발급 API 테스트")
    @Test
    @WithMockUser
    void givenRequest_whenReissueToken_then200() throws Exception {
        //given
        TokenReissueDto tokenReissueDto = TestEntityUtils.tokenReissueDto();
        LoginToken loginToken = TestEntityUtils.loginToken();
        when(userService.reissueToken(tokenReissueDto)).thenReturn(loginToken);
        //when & then
        mockMvc.perform(post("/users/login/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(tokenReissueDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("accessToken").value("Bearer sample-access-token"))
                .andExpect(jsonPath("refreshToken").value("Bearer sample-refresh-token"));

        verify(userService, times(1)).reissueToken(tokenReissueDto);
    }

    @DisplayName("내가 쓴 글 조회 API 테스트")
    @Test
    @WithMockAuthUser
    void givenUserId_whenGetMyPost_thenOK() throws Exception {
        //given
        Long userId = 1L;
        ResponsePostDto responsePostDto = TestEntityUtils.responsePostDto();
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.getMyPost(userId, "", pageable)).thenReturn(responsePostDto);
        //when & then
        mockMvc.perform(get("/users/posts")
                        .param("page", "1")
                        .param("type", "question")

                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(userService, times(1)).getMyPost(userId, "question", pageable);

    }

    @DisplayName("마이페이지 정보 조회 API 테스트")
    @Test
    @WithMockAuthUser
    void givenUserId_whenGetMe_then200() throws Exception {
        //given
        Long userId = 1L;
        ResponseUserInfoDto responseUserInfoDto = TestEntityUtils.responseUserInfoDto();
        when(userService.getUserInfo(userId)).thenReturn(responseUserInfoDto);
        //when & then
        mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(userService, times(1)).getUserInfo(userId);

    }


    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/me")
    public Response<?> me(@AuthenticationPrincipal JwtUserDetails user) {
        ResponseUserInfoDto userInfo = userService.getUserInfo(user.getUserId());
        return Response.success(userInfo);
    }
}

