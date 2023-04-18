package com.team.puddy.domain.review.repository;

import com.team.puddy.domain.review.domain.Review;
import com.team.puddy.domain.review.repository.querydsl.ReviewQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,Long>, ReviewQueryRepository {
}
