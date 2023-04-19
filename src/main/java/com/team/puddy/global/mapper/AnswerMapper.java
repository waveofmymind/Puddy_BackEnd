package com.team.puddy.global.mapper;

import com.team.puddy.domain.answer.domain.Answer;
import com.team.puddy.domain.answer.dto.RequestAnswerDto;
import com.team.puddy.domain.answer.dto.ResponseAnswerDto;
import com.team.puddy.domain.answer.dto.ResponseAnswerDtoExcludeUser;
import com.team.puddy.domain.question.domain.Question;
import com.team.puddy.domain.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AnswerMapper {

    default Answer toEntity(RequestAnswerDto answerDto, User user, Question question) {
        return Answer.builder()
                .content(answerDto.content())
                .postCategory(answerDto.postCategory())
                .selected(false)
                .user(user)
                .question(question).build();
    }
    @Mapping(target = "nickname",ignore = true)
    @Mapping(target = "userRole",ignore = true)
    ResponseAnswerDtoExcludeUser toDto(Answer answer);

    default ResponseAnswerDto toDto(Answer answer, User user) {
        return ResponseAnswerDto.builder()
                .id(answer.getId())
                .userId(user.getId())
                .content(answer.getContent())
                .nickname(user.getNickname())
                .selected(answer.isSelected())
                .userRole(user.getRole())
                .createDate(answer.getCreatedDate())
                .postCategory(answer.getPostCategory()).build();
    }
}
