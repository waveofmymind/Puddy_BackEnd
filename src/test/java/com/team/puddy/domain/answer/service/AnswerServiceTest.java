package com.team.puddy.domain.answer.service;

import com.team.puddy.domain.TestEntityUtils;
import com.team.puddy.domain.answer.domain.Answer;
import com.team.puddy.domain.answer.dto.RequestAnswerDto;
import com.team.puddy.domain.answer.dto.request.UpdateAnswerDto;
import com.team.puddy.domain.answer.repository.AnswerRepository;
import com.team.puddy.domain.question.domain.Question;
import com.team.puddy.domain.question.repository.QuestionRepository;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.repository.UserRepository;
import com.team.puddy.global.mapper.AnswerMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;


import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("답변 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class AnswerServiceTest {

    @InjectMocks
    private AnswerService answerService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private AnswerMapper answerMapper;

    @DisplayName("답변 추가 테스트")
    @Test
    public void givenRequest_whenAddAnswer_thenOK() {
        Long userId = 1L;
        Long questionId = 1L;
        RequestAnswerDto requestAnswerDto = TestEntityUtils.requestAnswerDto();
        User user = TestEntityUtils.user();
        Question question = TestEntityUtils.question(user);
        Answer answer = TestEntityUtils.answer();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(questionRepository.findById(questionId)).willReturn(Optional.of(question));
        given(answerMapper.toEntity(requestAnswerDto, user, question)).willReturn(answer);

        answerService.addAnswer(requestAnswerDto, userId, questionId);

        verify(userRepository, times(1)).findById(userId);
        verify(questionRepository, times(1)).findById(questionId);
        verify(answerMapper, times(1)).toEntity(requestAnswerDto, user, question);
        verify(answerRepository, times(1)).save(any(Answer.class));
    }

    @DisplayName("답변 채택 테스트")
    @Test
    public void givenAnswerId_whenSelectAnswer_thenOK() {
        Long userId = 1L;
        Long questionId = 1L;
        Long answerId = 1L;
        User user = TestEntityUtils.user();
        Question question = TestEntityUtils.question(user);

        given(questionRepository.findByIdWithUser(questionId)).willReturn(Optional.of(question));

        answerService.selectAnswer(questionId, answerId, userId);

        verify(questionRepository, times(1)).findByIdWithUser(questionId);
        verify(questionRepository, times(1)).select(questionId);
        verify(answerRepository, times(1)).select(answerId);
    }

    @DisplayName("답변 수정 테스트")
    @Test
    public void givenUpdate_whenUpdateAnswer_thenOK() {
        Long userId = 1L;
        Long questionId = 1L;
        Long answerId = 1L;
        UpdateAnswerDto updateAnswerDto = TestEntityUtils.updateAnswerDto();
        User user = TestEntityUtils.user();
        Question question = TestEntityUtils.question(user);
        Answer answer = TestEntityUtils.answer();

        given(answerRepository.findAnswerForUpdate(answerId, userId, questionId)).willReturn(Optional.of(answer));

        answerService.updateAnswer(updateAnswerDto, answerId, questionId, userId);

        verify(answerRepository, times(1)).findAnswerForUpdate(answerId, userId, questionId);
        assertEquals("수정 내용", answer.getContent());
    }

    @DisplayName("답변 삭제 테스트")
    @Test
    public void givenAnswerId_whenDeleteAnswer_thenOK() {
        Long userId = 1L;
        Long questionId = 1L;
        User user = TestEntityUtils.user();
        Long answerId = 1L;
        given(answerRepository.existsAnswer(answerId, questionId, userId)).willReturn(true);

        answerService.deleteAnswer(answerId, questionId, userId);

        verify(answerRepository, times(1)).existsAnswer(answerId, questionId, userId);
        verify(answerRepository, times(1)).deleteById(answerId);
    }

}
