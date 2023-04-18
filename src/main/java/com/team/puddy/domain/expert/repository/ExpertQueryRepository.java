package com.team.puddy.domain.expert.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.puddy.domain.expert.domain.Expert;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.team.puddy.domain.expert.domain.QExpert.expert;
import static com.team.puddy.domain.image.domain.QImage.image;
import static com.team.puddy.domain.review.domain.QReview.review;
import static com.team.puddy.domain.user.domain.QUser.user;


public interface ExpertQueryRepository {

    Optional<Expert> findByIdWithReview(Long expertId);

    Optional<Expert> findByIdWithUser(Long expertId);

    List<Expert> findExpertListForMainPage();

    Slice<Expert> findExpertList(Pageable pageable);

}
