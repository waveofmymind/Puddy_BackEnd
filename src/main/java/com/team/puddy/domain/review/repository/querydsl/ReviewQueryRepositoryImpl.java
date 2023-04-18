package com.team.puddy.domain.review.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.puddy.domain.review.domain.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.team.puddy.domain.review.domain.QReview.review;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepositoryImpl implements ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Review> findReviewListByExpertId(Long expertId) {
        return queryFactory.selectFrom(review)
                .where(review.expert.id.eq(expertId))
                .fetch();
    }
}
