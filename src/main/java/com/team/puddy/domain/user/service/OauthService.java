package com.team.puddy.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team.puddy.domain.type.JwtProvider;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.dto.request.OauthUserRequest;
import com.team.puddy.domain.user.repository.UserRepository;
import com.team.puddy.global.config.auth.KakaoOauth2Utils;
import com.team.puddy.global.config.auth.OauthUserInfo;
import com.team.puddy.global.config.security.jwt.JwtTokenUtils;
import com.team.puddy.global.config.security.jwt.LoginToken;
import com.team.puddy.global.error.ErrorCode;
import com.team.puddy.global.error.exception.NotFoundException;
import com.team.puddy.global.error.exception.oauth.ProviderNotSupportedException;
import com.team.puddy.global.error.exception.oauth.NeedInfoRequiredException;
import com.team.puddy.global.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Transactional
    public LoginToken loginOauthUser(OauthUserInfo userInfo) {
        Optional<User> optionalUser = userRepository.findByAccountAndEmail(userInfo.sub(), userInfo.email());
        //TODO: 중복 없애려면 이메일만으로 찾아서 계정이 없으면 생성하고 있으면 로그인
        User findUser = optionalUser.orElseThrow(NeedInfoRequiredException::new);

        return jwtTokenUtils.createLoginToken(findUser);
    }



    @Transactional
    public LoginToken JoinOauthUser(String accessToken, OauthUserRequest userRequest) throws JsonProcessingException {
        OauthUserInfo oauthUserInfo;
        JwtProvider jwtProvider = JwtProvider.valueOf(userRequest.provider());
        oauthUserInfo = switch (jwtProvider) {
            case KAKAO -> KakaoOauth2Utils.getKakaoUserInfo(accessToken);
            //TODO: 네이버, 구글
            case PUDDY -> throw new ProviderNotSupportedException();

            default -> throw new ProviderNotSupportedException();
        };

        User user = userMapper.toEntityFromOauth(oauthUserInfo,userRequest);
        userRepository.save(user);
        return jwtTokenUtils.createLoginToken(user);
    }



}
