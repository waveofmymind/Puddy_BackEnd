package com.team.puddy.domain.expert.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.puddy.domain.expert.domain.Expert;
import com.team.puddy.domain.expert.repository.ExpertQueryRepository;
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

@Repository
@RequiredArgsConstructor
public class ExpertQueryRepositoryImpl implements ExpertQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Expert> findByIdWithReview(Long expertId) {
        return Optional.ofNullable(queryFactory.selectFrom(expert)
                .leftJoin(expert.reviewList, review).fetchJoin()
                .where(expert.id.eq(expertId))
                .fetchFirst());
    }

    public Optional<Expert> findByIdWithUser(Long expertId) {
        return Optional.ofNullable(queryFactory.selectFrom(expert)
                .leftJoin(expert.user,user).fetchJoin()
                .leftJoin(user.image,image).fetchJoin()
                .leftJoin(expert.reviewList, review).fetchJoin()
                .where(expert.id.eq(expertId))
                .fetchFirst());
    }

    public List<Expert> findExpertListForMainPage() {
        return queryFactory.select(expert)
                .from(expert)
                .leftJoin(expert.user, user).fetchJoin()
                .leftJoin(user.image, image).fetchJoin()
                .orderBy(expert.modifiedDate.desc())
                .limit(5).fetch();
    }

    public Slice<Expert> findExpertList(Pageable pageable) {
        List<Expert> expertList = queryFactory.selectFrom(expert)
                .join(expert.user, user).fetchJoin()
                .leftJoin(user.image, image).fetchJoin()
                .orderBy(expert.modifiedDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(expertList,pageable,expertList.size() > pageable.getPageSize());
    }

}
