package com.team.puddy.domain.user.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.team.puddy.domain.expert.domain.QExpert.expert;
import static com.team.puddy.domain.image.domain.QImage.image;
import static com.team.puddy.domain.pet.domain.QPet.pet;
import static com.team.puddy.domain.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public boolean isExistsEmail(String email) {
        return queryFactory.select(user.id)
                .from(user)
                .where(user.email.eq(email))
                .fetchOne() != null;
    }

    public boolean isExistsAccount(String account) {
        return queryFactory.select(user.id)
                .from(user)
                .where(user.account.eq(account))
                .fetchOne() != null;
    }

    //유저 정보 전문가 테이블만 조회
    public Optional<User> findByIdWithExpert(Long userId) {

        return Optional.ofNullable(queryFactory
                .selectFrom(user)
                .leftJoin(user.image, image).fetchJoin()
                .leftJoin(user.expert, expert).fetchJoin() // INNER JOIN을 사용한 페치 조인
                .where(user.id.eq(userId))
                .fetchOne());
    }

    public Optional<User> findByIdWithPet(Long userId) {

        return Optional.ofNullable(queryFactory
                .selectFrom(user)
                .leftJoin(user.pet, pet).fetchJoin() // INNER JOIN을 사용한 페치 조인
                .where(user.id.eq(userId))
                .fetchOne());
    }

    public boolean isExistsNickname(String nickname) {
        return queryFactory.select(user.id)
                .from(user)
                .where(user.nickname.eq(nickname))
                .fetchOne() != null;
    }

    //유저 정보 펫과 전문가만 조회
    public Optional<User> findByIdWithPetAndExpert(Long userId) {
        return Optional.ofNullable(queryFactory.selectFrom(user)
                .leftJoin(user.pet, pet).fetchJoin()
                .leftJoin(user.expert, expert).fetchJoin()
                .where(user.id.eq(userId))
                .fetchOne());
    }
    //삭제 로직 고치기
    //유저 정보 전체 페치조인 조회
    public Optional<User> findByUserId(Long userId) {
        return Optional.ofNullable(queryFactory.selectFrom(user)
                .leftJoin(user.expert, expert).fetchJoin()
                .leftJoin(user.image, image).fetchJoin()
                .leftJoin(user.pet, pet).fetchJoin()
                .where(user.id.eq(userId))
                .fetchOne());
    }

    public Optional<User> findByAccount(String account) {
        return Optional.ofNullable(queryFactory.selectFrom(user)
                .leftJoin(user.expert, expert).fetchJoin()
                .where(user.account.eq(account))
                .fetchOne());
    }

}
