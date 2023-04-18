package com.team.puddy.domain.user.repository;

import  com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.puddy.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.team.puddy.domain.expert.domain.QExpert.expert;
import static com.team.puddy.domain.image.domain.QImage.image;
import static com.team.puddy.domain.pet.domain.QPet.pet;
import static com.team.puddy.domain.user.domain.QUser.user;


public interface UserQueryRepository {

    public boolean isExistsEmail(String email);

    public boolean isExistsAccount(String account);

    //유저 정보 전문가 테이블만 조회
    public Optional<User> findByIdWithExpert(Long userId);
    public Optional<User> findByIdWithPet(Long userId);

    public boolean isExistsNickname(String nickname);

    //유저 정보 펫과 전문가만 조회
    public Optional<User> findByIdWithPetAndExpert(Long userId);

    //유저 정보 전체 페치조인 조회
    public Optional<User> findByUserId(Long userId);

    public Optional<User> findByAccount(String account);
}
