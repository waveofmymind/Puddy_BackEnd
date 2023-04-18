package com.team.puddy.domain.expert.service;

import com.team.puddy.domain.TestEntityUtils;
import com.team.puddy.domain.expert.domain.Expert;
import com.team.puddy.domain.expert.dto.RequestExpertDto;
import com.team.puddy.domain.expert.dto.ResponseExpertDto;
import com.team.puddy.domain.expert.repository.ExpertRepository;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.repository.UserRepository;
import com.team.puddy.global.mapper.ExpertMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("전문가 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class ExpertServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpertRepository expertRepository;

    @Mock
    private ExpertMapper expertMapper;

    @InjectMocks
    private ExpertService expertService;

    @DisplayName("전문가 등록 테스트")
    @Test
    void givenRequest_whenRegisterExpert_thenOK() {

        Long userId = 1L;
        RequestExpertDto requestExpertDto = TestEntityUtils.requestExpertDto();
        User user = TestEntityUtils.user();
        Expert expert = TestEntityUtils.expert();

        when(userRepository.findByIdWithExpert(userId)).thenReturn(java.util.Optional.of(user));

        when(expertMapper.toEntity(requestExpertDto, user)).thenReturn(expert);

        expertService.registerExpert(requestExpertDto, userId);

        verify(userRepository).findByIdWithExpert(userId);
        verify(expertMapper).toEntity(requestExpertDto, user);
        verify(expertRepository).save(expert);
    }

    @DisplayName("전문가 조회 테스트")
    @Test
    public void givenUserId_whenFindExpert_thenOK() {

        Long userId = 1L;
        ResponseExpertDto responseExpertDto = TestEntityUtils.responseExpertDto();
        Expert expert = TestEntityUtils.expert();
        given(expertRepository.findByIdWithUser(1L)).willReturn(Optional.of(expert));

        when(expertRepository.findByIdWithUser(userId)).thenReturn(java.util.Optional.of(expert));
        when(expertMapper.toDto(expert, "")).thenReturn(responseExpertDto);

        expertService.getExpertByUserId(userId);

        verify(expertRepository).findByIdWithUser(userId);
        verify(expertMapper).toDto(expert, "");
    }


}
