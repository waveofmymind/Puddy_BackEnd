package com.team.puddy.domain.pet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.puddy.domain.TestEntityUtils;
import com.team.puddy.domain.pet.dto.request.RequestPetDto;
import com.team.puddy.domain.pet.dto.request.UpdatePetDto;
import com.team.puddy.domain.pet.dto.response.ResponsePetDto;
import com.team.puddy.domain.pet.service.PetService;
import com.team.puddy.domain.question.controller.QuestionController;
import com.team.puddy.domain.question.service.QuestionService;
import com.team.puddy.domain.user.service.UserService;
import com.team.puddy.global.common.S3UpdateUtil;
import com.team.puddy.global.common.dto.Response;
import com.team.puddy.global.config.WithMockAuthUser;
import com.team.puddy.global.config.auth.JwtUserDetails;
import com.team.puddy.global.config.security.SecurityConfig;
import com.team.puddy.global.config.security.jwt.JwtAuthorizationFilter;
import com.team.puddy.global.config.security.jwt.JwtVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static org.mockito.ArgumentMatchers.any;


import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PetController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtVerifier.class)
        })
@DisplayName("펫 API 테스트")
public class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PetService petService;

    @MockBean
    private UserService userService;

    @MockBean
    private S3UpdateUtil s3UpdateUtil;

    @DisplayName("펫 등록 API")
    @Test
    @WithMockAuthUser
    void givenRequest_whenAddPet_then201() throws Exception {
        //given
        RequestPetDto requestDto = TestEntityUtils.requestPetDto();
        MockMultipartFile image = TestEntityUtils.mockMultipartFile();

        doNothing().when(petService).addPet(anyLong(),any(MultipartFile.class),any(RequestPetDto.class));
        MockPart requestPart = new MockPart("request", objectMapper.writeValueAsString(requestDto).getBytes());
        requestPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        //when & then
        mockMvc.perform(multipart("/users/pets")
                        .file(image)
                        .part(requestPart)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isCreated());

        verify(petService).addPet(anyLong(),any(MultipartFile.class),any(RequestPetDto.class));
    }

    @DisplayName("펫 상세 조회 API 테스트")
    @Test
    @WithMockAuthUser
    void givenUserId_whenGetPetDetail_thenOk() throws Exception {
        //given
        ResponsePetDto responsePetDto = TestEntityUtils.responsePetDto();
        when(petService.getPetByUserId(anyLong())).thenReturn(responsePetDto);

        //when & then
        mockMvc.perform(get("/users/pets/detail")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value(responsePetDto.name()))
                .andExpect(jsonPath("$.data.age").value(responsePetDto.age()))
                .andExpect(jsonPath("$.data.breed").value(responsePetDto.breed()))
                .andExpect(jsonPath("$.data.isNeutered").value(responsePetDto.isNeutered()))
                .andExpect(jsonPath("$.data.weight").value(responsePetDto.weight()))
                .andExpect(jsonPath("$.data.gender").value(responsePetDto.gender()))
                .andExpect(jsonPath("$.data.note").value(responsePetDto.note()));

        verify(petService, times(1)).getPetByUserId(anyLong());
    }

    @DisplayName("펫 정보 수정 API 테스트")
    @Test
    @WithMockAuthUser
    void givenUserId_whenUpdatePet_thenOk() throws Exception {
        //given
        UpdatePetDto updatePetDto = TestEntityUtils.updatePetDto();
        MockMultipartFile image = TestEntityUtils.mockMultipartFile();
        MockPart updatePart = new MockPart("request", objectMapper.writeValueAsString(updatePetDto).getBytes());
        updatePart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        doNothing().when(petService).updatePet(any(UpdatePetDto.class), anyLong(), any(MultipartFile.class));

        //when & then
        mockMvc.perform(multipart("/users/pets/update")
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

        verify(petService, times(1)).updatePet(any(UpdatePetDto.class), anyLong(), any(MultipartFile.class));
    }

}

