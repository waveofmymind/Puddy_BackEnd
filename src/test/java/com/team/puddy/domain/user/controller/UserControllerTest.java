package com.team.puddy.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.puddy.domain.TestEntityUtils;
import com.team.puddy.domain.user.dto.request.LoginUserRequest;
import com.team.puddy.domain.user.dto.request.RegisterUserRequest;
import com.team.puddy.domain.user.dto.response.ResponsePostDto;
import com.team.puddy.domain.user.service.UserService;
import com.team.puddy.global.common.dto.Response;
import com.team.puddy.global.config.WithMockAuthUser;
import com.team.puddy.global.config.auth.JwtUserDetails;
import com.team.puddy.global.config.security.SecurityConfig;
import com.team.puddy.global.config.security.jwt.JwtAuthorizationFilter;
import com.team.puddy.global.config.security.jwt.JwtVerifier;
import com.team.puddy.global.config.security.jwt.LoginToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class,
excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtVerifier.class)
})
@DisplayName("유저 API 테스트")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;


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
