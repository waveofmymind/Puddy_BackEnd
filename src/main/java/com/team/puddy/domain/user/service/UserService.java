package com.team.puddy.domain.user.service;

import com.team.puddy.domain.answer.domain.Answer;
import com.team.puddy.domain.answer.dto.ResponseAnswerDtoExcludeUser;
import com.team.puddy.domain.answer.repository.AnswerRepository;
import com.team.puddy.domain.answer.repository.querydsl.AnswerQueryRepository;
import com.team.puddy.domain.article.domain.Article;
import com.team.puddy.domain.article.dto.response.ResponseArticleExcludeCommentDto;
import com.team.puddy.domain.article.repository.ArticleRepository;
import com.team.puddy.domain.comment.repository.CommentRepository;
import com.team.puddy.domain.image.domain.Image;
import com.team.puddy.domain.image.service.ImageService;
import com.team.puddy.domain.question.domain.Question;
import com.team.puddy.domain.question.dto.response.ResponseQuestionExcludeAnswerDto;
import com.team.puddy.domain.question.repository.QuestionRepository;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.dto.request.FindAccountDto;
import com.team.puddy.domain.user.dto.request.LoginUserRequest;
import com.team.puddy.domain.user.dto.request.RegisterUserRequest;
import com.team.puddy.domain.user.dto.response.ResponsePostDto;
import com.team.puddy.domain.user.dto.response.ResponseUserInfoDto;
import com.team.puddy.domain.user.dto.response.TokenReissueDto;
import com.team.puddy.domain.user.repository.UserRepository;
import com.team.puddy.global.config.security.jwt.JwtVerifier;
import com.team.puddy.global.error.ErrorCode;
import com.team.puddy.global.error.exception.BusinessException;
import com.team.puddy.global.error.exception.NotFoundException;
import com.team.puddy.global.mapper.AnswerMapper;
import com.team.puddy.global.mapper.ArticleMapper;
import com.team.puddy.global.mapper.QuestionMapper;
import com.team.puddy.global.mapper.UserMapper;
import com.team.puddy.global.config.security.jwt.JwtTokenUtils;
import com.team.puddy.global.config.security.jwt.LoginToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.team.puddy.global.error.ErrorCode.*;
import static com.team.puddy.global.error.ErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final ArticleRepository articleRepository;

    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final ImageService imageService;


    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final JwtVerifier jwtVerifier;

    @Transactional
    public LoginToken login(LoginUserRequest request) {
        User user = userRepository.findByAccount(request.account()).orElseThrow(() -> new NotFoundException(INVALID_ACCOUNT));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(INVALID_PASSWORD);
        }

        // 토큰 생성

        return jwtTokenUtils.createLoginToken(user);
    }

    @Transactional
    public void join(RegisterUserRequest request) {
        //회원가입된 유저가 있는지
        userRepository.findByAccount(request.account()).ifPresent(it -> {
            throw new BusinessException(DUPLICATE_ACCOUNT, String.format("%s is duplicated", request.account()));
        });

        //회원가입 진행
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

    }

    @Transactional
    public LoginToken reissueToken(TokenReissueDto tokenReissueDto) {
        String account = jwtVerifier.parseAccount(tokenReissueDto.accessToken());
        jwtVerifier.verifyRefreshToken(account, tokenReissueDto.refreshToken());
        User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        return jwtTokenUtils.createLoginToken(user);
    }


    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        jwtVerifier.expireRefreshToken(user.getAccount());
    }

    @Transactional(readOnly = true)
    public ResponseUserInfoDto getUserInfo(Long userId) {
        User findUser = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        Image userImage = findUser.getImage();
        boolean hasPet = findUser.getPet() != null;
        if (userImage == null) { // 해당 유저의 이미지가 없는 경우
            return userMapper.toDto(findUser,"",hasPet);
        }
        return userMapper.toDto(findUser, userImage.getImagePath(),hasPet);
    }


    @Transactional
    public void updateProfile(Long userId, String nickname, MultipartFile file) throws IOException {
        User findUser = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        imageService.updateImageUser(findUser, file);
        findUser.setNickname(nickname);
    }

    @Transactional
    public void updateAuth(Long userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        findUser.updateAuth();
    }


    @Transactional(readOnly = true)
    public List<ResponseQuestionExcludeAnswerDto> getMyQuestion(Long userId, Pageable page) {
        Slice<Question> questionList = questionRepository.findQuestionListByUserId(userId,page);
        return questionList.stream().map(questionMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ResponseArticleExcludeCommentDto> getMyArticle(Long userId, Pageable page) {
        Slice<Article> articleList = articleRepository.findArticleListByUserId(userId,page);
        return articleList.stream().map(articleMapper::toExcludeImageDto).toList();
    }




    @Transactional(readOnly = true)
    public void duplicateEmailCheck(String email) {
        if (userRepository.isExistsEmail(email)) {
            log.error("중복 에러 발생");
            throw new BusinessException(DUPLICATE_EMAIL, DUPLICATE_EMAIL.getMessage());
        }

    }

    @Transactional(readOnly = true)
    public void duplicateAccountCheck(String account) {
        if (userRepository.isExistsAccount(account)) {
            throw new BusinessException(DUPLICATE_ACCOUNT, DUPLICATE_ACCOUNT.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public void duplicateNicknameCheck(String nickname) {
        if (userRepository.isExistsNickname(nickname)) {
            log.error("중복 에러 발생");
            throw new BusinessException(DUPLICATE_NICKNAME, DUPLICATE_NICKNAME.getMessage());
        }

    }

    @Transactional(readOnly = true)
    public boolean checkHasPet(Long userId) {
        return userRepository.findByIdWithPet(userId).orElseThrow(() -> new BusinessException(USER_NOT_FOUND))
                .getPet() != null; // 펫이 있으면 true, 없으면 false
    }


    @Transactional(readOnly = true)
    public String findAccount(FindAccountDto accountDto) {
        User findUser = userRepository.findByUsernameAndEmail(accountDto.username(), accountDto.email())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        return findUser.getAccount();
    }

    @Transactional
    public void changePassword(String password, Long userId) {
        User findUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(CHANGE_PASSWORD_FAIL));
        findUser.updatePassword(passwordEncoder.encode(password));
    }

    @Transactional(readOnly = true)
    public ResponsePostDto getMyPost(Long userId, String type, Pageable pageable) {
        return switch(type) {
            case "question" -> {
                Slice<Question> questionSlice = questionRepository.findQuestionListByUserId(userId, pageable);
                List<ResponseQuestionExcludeAnswerDto> questionList = questionSlice.stream().map(questionMapper::toDto).toList();
                yield ResponsePostDto.builder()
                        .questionList(questionList).build();
            }
            case "article" -> {
                Slice<Article> articleSlice = articleRepository.findArticleListByUserId(userId, pageable);
                List<ResponseArticleExcludeCommentDto> articleList = articleSlice.stream().map(articleMapper::toExcludeImageDto).toList();
                yield ResponsePostDto.builder()
                        .articleList(articleList).build();
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };
    }
}
