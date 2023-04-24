package com.team.puddy.global.mapper;

import com.team.puddy.domain.image.domain.Image;
import com.team.puddy.domain.type.JwtProvider;
import com.team.puddy.domain.type.UserRole;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.dto.request.OauthUserRequest;
import com.team.puddy.domain.user.dto.request.RegisterUserRequest;
import com.team.puddy.domain.user.dto.response.ResponseUserInfoDto;
import com.team.puddy.global.config.auth.KakaoUserDetails;
import com.team.puddy.global.config.auth.OauthUserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Random;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    default String createNickname() {
        Random random = new Random();
        int randomNumber = random.nextInt(100000); // 랜덤한 5자리 숫자 생성 (~100000)
        return "퍼디" + randomNumber;
    }

    default User toEntity(RegisterUserRequest request){
        return User.builder()
                .account(request.account())
                .password(request.password())
                .email(request.email())
                .username(request.username())
                .provider(JwtProvider.PUDDY)
                .role(UserRole.USER.getRole())
                .isNotificated(request.isNotificated())
                .nickname(createNickname())
                .build();
    }
    @Mapping(target = "imagePath",source = "imagePath")
    ResponseUserInfoDto toDto(User user,String imagePath, boolean hasPet, boolean hasExpertInfo);

    default User toEntityFromOauth(OauthUserInfo user, OauthUserRequest oauthUserRequest) {
        return User.builder()
                .account(user.sub())
                .email(user.email())
                .role(UserRole.USER.getRole())
                .provider(JwtProvider.valueOf(oauthUserRequest.provider()))
                .isNotificated(oauthUserRequest.isNotificated())
                .nickname(createNickname())
                .username(user.name()).build();
    }
}
