package com.team.puddy.domain.question.controller;


import com.team.puddy.domain.question.dto.response.QuestionListResponseDto;
import com.team.puddy.domain.question.dto.response.QuestionResponseDto;
import com.team.puddy.domain.question.dto.request.RequestQuestionDto;
import com.team.puddy.domain.question.dto.request.UpdateQuestionDto;
import com.team.puddy.domain.question.service.QuestionService;
import com.team.puddy.global.common.dto.Response;
import com.team.puddy.global.config.auth.JwtUserDetails;
import com.team.puddy.global.mapper.QuestionMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class QuestionController {


    private final QuestionService questionService;

    private final QuestionMapper questionMapper;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "QNA 게시글 등록 메서드")
    public Response<?> registerQuestion(@RequestPart("request") RequestQuestionDto request,
                                        @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                        @AuthenticationPrincipal JwtUserDetails user) {
        questionService.addQuestion(questionMapper.toServiceDto(request), images, user.getUserId());

        return Response.success();
    }

    @PutMapping(value = "/{questionId}")
    public Response<?> updateQuestion(@PathVariable("questionId") Long questionId,
                                      @RequestPart("request") UpdateQuestionDto requestDto,
                                      @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                      @AuthenticationPrincipal JwtUserDetails user) {

        questionService.updateQuestion(questionId, requestDto, images, user.getUserId());
        return Response.success();

    }

    @DeleteMapping("/{questionId}")
    public Response<?> deleteQuestion(@PathVariable("questionId") Long questionId,
                                      @AuthenticationPrincipal JwtUserDetails user) {
        questionService.deleteQuestion(questionId, user.getUserId());

        return Response.success();
    }


    @GetMapping("/{questionId}")
    @Operation(summary = "QNA 게시글 단건 조회 메서드")
    public Response<QuestionResponseDto> getQuestion(@PathVariable Long questionId) {
        questionService.increaseViewCount(questionId);
        QuestionResponseDto question = questionService.getQuestion(questionId);
        return Response.success(question);
    }

    @GetMapping
    @Operation(summary = "QNA 리스트 조회 메서드")
    public Response<QuestionListResponseDto> getQuestionList(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                                             @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                             @RequestParam int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);

        QuestionListResponseDto questionListDto = questionService.getQuestionListByTitleStartWith(pageable, keyword, sort);

        return Response.success(questionListDto);
    }

    @GetMapping("/count")
    public Response<Long> getQuestionCount(@AuthenticationPrincipal JwtUserDetails jwtUserDetails) {
        return Response.success(questionService.getQuestionCount());
    }


}
