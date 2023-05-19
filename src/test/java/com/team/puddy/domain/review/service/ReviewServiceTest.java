package com.team.puddy.domain.review.service;

import com.team.puddy.domain.TestEntityUtils;
import com.team.puddy.domain.expert.domain.Expert;
import com.team.puddy.domain.expert.repository.ExpertRepository;
import com.team.puddy.domain.review.domain.Review;
import com.team.puddy.domain.review.dto.RequestReviewDto;
import com.team.puddy.domain.review.dto.ResponseReviewDto;
import com.team.puddy.domain.review.repository.ReviewRepository;
import com.team.puddy.global.mapper.ReviewMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("리뷰 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ExpertRepository expertRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    @DisplayName("필수 정보로 리뷰 작성시 성공한다")
    @Test
    public void givenRequest_whenAddReview_thenOK() {

        Long expertId = 1L;
        RequestReviewDto requestReviewDto = TestEntityUtils.requestReviewDto();
        Expert expert = TestEntityUtils.expert();
        Review review = TestEntityUtils.review();
        when(expertRepository.findByIdWithReview(expertId)).thenReturn(java.util.Optional.of(expert));
        when(reviewMapper.toEntity(requestReviewDto, expert)).thenReturn(review);

        reviewService.addReview(expertId, requestReviewDto);

        verify(expertRepository).findByIdWithReview(expertId);
        verify(reviewMapper).toEntity(requestReviewDto, expert);
        verify(reviewRepository).save(review);
    }
    @DisplayName("유효한 전문가 id로 리뷰 리스트 조회시 성공한다")
    @Test
    public void givenExpertId_whenGetReviewListByExpertId_thenOK() {
        Long expertId = 1L;
        ResponseReviewDto responseReviewDto = TestEntityUtils.responseReviewDto();
        Review review = TestEntityUtils.review();
        List<Review> reviewList = Collections.singletonList(review);
        when(reviewRepository.findReviewListByExpertId(expertId)).thenReturn(reviewList);
        when(reviewMapper.toDto(any(Review.class))).thenReturn(responseReviewDto);

        List<ResponseReviewDto> result = reviewService.getReviewListByExpertId(expertId);

        verify(reviewRepository).findReviewListByExpertId(expertId);
        verify(reviewMapper, times(reviewList.size())).toDto(any(Review.class));
    }

}