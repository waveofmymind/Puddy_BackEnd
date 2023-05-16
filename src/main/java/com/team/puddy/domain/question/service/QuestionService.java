package com.team.puddy.domain.question.service;

import com.team.puddy.domain.image.domain.Image;
import com.team.puddy.domain.image.service.ImageService;
import com.team.puddy.domain.pet.domain.Pet;
import com.team.puddy.domain.question.domain.Question;
import com.team.puddy.domain.question.dto.request.QuestionServiceRegister;
import com.team.puddy.domain.question.dto.request.RequestQuestionDto;
import com.team.puddy.domain.question.dto.response.QuestionListResponseDto;
import com.team.puddy.domain.question.dto.response.QuestionResponseDto;
import com.team.puddy.domain.question.repository.QuestionRepository;
import com.team.puddy.domain.question.dto.request.UpdateQuestionDto;
import com.team.puddy.domain.question.dto.response.ResponseQuestionExcludeAnswerDto;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.repository.UserRepository;
import com.team.puddy.global.error.ErrorCode;
import com.team.puddy.global.error.exception.NotFoundException;
import com.team.puddy.global.mapper.AnswerMapper;
import com.team.puddy.global.mapper.QuestionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuestionService {


    private final QuestionRepository questionRepository;

    private final UserRepository userRepository;

    private final QuestionMapper questionMapper;

    private final AnswerMapper answerMapper;

    private final ImageService imageService;


    @Transactional(readOnly = true)
    public QuestionListResponseDto getQuestionListByTitleStartWith(Pageable page,String keyword,String sort) {
        Slice<Question> questionList = switch (sort) {
            case "asc" -> questionRepository.findByTitleStartWithOrderByCreatedDate(page, keyword);
            case "viewCount" -> questionRepository.findByTitleStartWithOrderByViewCountDesc(page, keyword);
            default -> questionRepository.findByTitleStartWithOrderByCreatedDateDesc(page, keyword);
        };
        return questionMapper.toDto(questionList.stream().map(questionMapper::toDto).toList(),questionList.hasNext());
    }

    @Transactional(readOnly = true)
    public List<ResponseQuestionExcludeAnswerDto> getPopularQuestions() {
        List<Question> popularQuestions = questionRepository.getPopularQuestionList();
        return popularQuestions.stream()
                .map(questionMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResponseQuestionExcludeAnswerDto> getRecentQuestions() {
        List<Question> recentQuestionList = questionRepository.getRecentQuestionList();
        return recentQuestionList.stream()
                .map(questionMapper::toDto)
                .toList();
    }


    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestion(Long questionId) {
        Question findquestion = questionRepository.getQuestion(questionId).orElseThrow(
                () -> new NotFoundException(ErrorCode.QUESTION_NOT_FOUND));

        Pet findpet = findquestion.getUser().getPet(); // Pet이 없으면 null 반환
        return questionMapper.toDto(findquestion, findpet, findquestion.getAnswerList().stream()
                .map(answer -> answerMapper.toDto(answer, answer.getUser())).toList());
    }

    @Transactional
    public void increaseViewCount(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new NotFoundException(ErrorCode.QUESTION_NOT_FOUND);
        }
        questionRepository.increaseViewCount(questionId);
    }

    @Transactional(readOnly = true)
    public long getQuestionCount() {
        return questionRepository.count();
    }

    @Transactional
    public void addQuestion(QuestionServiceRegister request, List<MultipartFile> images, Long userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        List<Image> imageList = imageService.saveImageListForQuestion(images);

        Question question = questionMapper.toEntity(request, imageList, findUser);
        questionRepository.save(question);

    }

    @Transactional
    public void updateQuestion(Long questionId, UpdateQuestionDto requestDto, List<MultipartFile> images, Long userId) {
        Question findQuestion = questionRepository.findQuestionForModify(questionId, userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.UNAUTHORIZED_OPERATION));

        imageService.updateImageListForQuestion(findQuestion, images);

        findQuestion.updateQuestion(requestDto.title(), requestDto.content(), requestDto.category());

    }

    @Transactional
    public void deleteQuestion(Long questionId, Long userId) {
        Question findQuestion = questionRepository.findQuestionForModify(questionId, userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.UNAUTHORIZED_OPERATION));
        //S3에 저장된 이미지를 지운다.
        List<Image> findImages = findQuestion.getImageList();

        imageService.deleteImageListFromQuestion(findImages);

        //질문글을 지운다.
        questionRepository.deleteById(questionId);
    }
}
