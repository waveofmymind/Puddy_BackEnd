package com.team.puddy.domain.review.repository.querydsl;

import com.team.puddy.domain.review.domain.Review;

import java.util.List;

public interface ReviewQueryRepository {

    public List<Review> findReviewListByExpertId(Long expertId);
}
