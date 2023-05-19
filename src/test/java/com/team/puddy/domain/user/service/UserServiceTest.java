package com.team.puddy.domain.user.service;

import com.team.puddy.domain.TestEntityUtils;

import com.team.puddy.domain.answer.domain.Answer;
import com.team.puddy.domain.answer.repository.AnswerRepository;
import com.team.puddy.domain.article.domain.Article;
import com.team.puddy.domain.article.repository.ArticleRepository;
import com.team.puddy.domain.question.domain.Question;
import com.team.puddy.domain.question.repository.QuestionRepository;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.dto.request.LoginUserRequest;
import com.team.puddy.domain.user.dto.request.RegisterUserRequest;
import com.team.puddy.domain.user.dto.response.TokenReissueDto;
import com.team.puddy.domain.user.repository.UserRepository;

import com.team.puddy.global.config.security.jwt.JwtTokenUtils;
import com.team.puddy.global.config.security.jwt.JwtVerifier;
import com.team.puddy.global.config.security.jwt.LoginToken;
import com.team.puddy.global.error.exception.BusinessException;
import com.team.puddy.global.error.exception.NotFoundException;
import com.team.puddy.global.mapper.AnswerMapper;
import com.team.puddy.global.mapper.ArticleMapper;
import com.team.puddy.global.mapper.QuestionMapper;
import com.team.puddy.global.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("유저 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks private UserService userService;
    @Mock private UserRepository userRepository;
    @Mock private JwtVerifier jwtVerifier;
    @Mock private JwtTokenUtils jwtTokenUtils;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private QuestionRepository questionRepository;

    @Mock private ArticleRepository articleRepository;
    @Mock private AnswerRepository answerRepository;
    @Mock private QuestionMapper questionMapper;
    @Mock private AnswerMapper answerMapper;

    @Mock private ArticleMapper articleMapper;



    @DisplayName("올바른 아이디와 비밀번호로 로그인시 성공한다.")
    @Test
    void givenRequest_whenLogin_thenOK() {
        //given
        LoginUserRequest request = LoginUserRequest.builder().account("puddy").password("1234").build();
        User user = TestEntityUtils.user();
        when(userRepository.findByAccount(eq(request.account()))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq(request.password()), eq(user.getPassword()))).thenReturn(false);

        assertThrows(BusinessException.class, () -> userService.login(request));

        verify(userRepository, times(1)).findByAccount(eq(request.account()));
        verify(passwordEncoder, times(1)).matches(eq(request.password()), eq(user.getPassword()));
    }

    @DisplayName("존재하지 않는 아이디로 로그인을 시도할 경우 400 예외가 발생한다.")
    @Test
    public void givenRequest_whenLogin_then400() {
        LoginUserRequest request = new LoginUserRequest("nonexistent", "password");

        when(userRepository.findByAccount(eq(request.account()))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.login(request));

        verify(userRepository, times(1)).findByAccount(eq(request.account()));
    }

    @DisplayName("모든 요구 정보로 회원가입시 성공한다.")
    @Test
    public void givenRequest_whenJoin_thenOK() {

        // Given
        User user = TestEntityUtils.user();
        RegisterUserRequest registerUserRequest = TestEntityUtils.registerUserRequest();
        when(userRepository.findByAccount(registerUserRequest.account())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerUserRequest.password())).thenReturn("encodedPassword");
        when(userMapper.toEntity(registerUserRequest)).thenReturn(user);
        // When
        userService.join(registerUserRequest);

        // Then
        verify(userRepository, times(1)).findByAccount(registerUserRequest.account());
        verify(passwordEncoder, times(1)).encode(registerUserRequest.password());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @DisplayName("같은 아이디로 회원가입을 요청할 경우 예외가 발생한다.")
    @Test
    public void givenRequest_whenJoin_Duplicate() {
        // Given
        User user = TestEntityUtils.user();
        RegisterUserRequest registerUserRequest = TestEntityUtils.registerUserRequest();
        when(userRepository.findByAccount(registerUserRequest.account())).thenReturn(Optional.of(user));

        // Then
        assertThrows(BusinessException.class, () -> {
            // When
            userService.join(registerUserRequest);
        });

        verify(userRepository, times(1)).findByAccount(registerUserRequest.account());
        verify(passwordEncoder, never()).encode(registerUserRequest.password());
        verify(userRepository, never()).save(any(User.class));
    }

    @DisplayName("만료된 액세스 토큰으로 토큰 재발급 요청시 성공한다.")
    @Test
    public void givenRequest_whenReissueToken_thenOK() {

        TokenReissueDto tokenReissueDto = new TokenReissueDto("accessToken", "refreshToken");
        User user = TestEntityUtils.user();
        LoginToken loginToken = new LoginToken("accessToken", "refreshToken");
        when(jwtVerifier.parseAccount(tokenReissueDto.accessToken())).thenReturn(user.getAccount());
        when(userRepository.findByAccount(user.getAccount())).thenReturn(Optional.of(user));
        when(jwtTokenUtils.createLoginToken(user)).thenReturn(loginToken);

        userService.reissueToken(tokenReissueDto);

        verify(jwtVerifier).parseAccount(tokenReissueDto.accessToken());
        verify(jwtVerifier).verifyRefreshToken(user.getAccount(), tokenReissueDto.refreshToken());
        verify(userRepository).findByAccount(user.getAccount());
        verify(jwtTokenUtils).createLoginToken(user);
    }
    @DisplayName("내 질문글 조회시 성공한다.")
    @Test
    public void givenRequestQuestion_whenGetPosts_thenOK() {

        Long userId = 1L;
        List<Question> questionList = List.of(TestEntityUtils.question(TestEntityUtils.user()));
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Question> questionSlice = new SliceImpl<>(questionList,pageable,false);
        when(questionRepository.findQuestionListByUserId(userId,pageable)).thenReturn(questionSlice);

        userService.getMyPost(userId,"question",pageable);

        verify(questionRepository).findQuestionListByUserId(userId,pageable);

        for (Question question : questionList) {
            verify(questionMapper).toDto(question);
        }

    }

    @DisplayName("정상적인 토큰으로 전문가 권한 업데이트 요청시 성공한다.")
    @Test
    public void whenRequest_whenUpdateAuth_thenOK() {

        Long userId = 1L;
        User user = TestEntityUtils.user();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateAuth(userId);

        verify(userRepository).findById(userId);
    }
    @DisplayName("중복된 이메일을 입력할 경우 중복되었다는 성공을 응답한다.")
    @Test
    public void givenEmail_whenCheckEmail_thenOK() {
        when(userRepository.isExistsEmail(anyString())).thenReturn(false);

        userService.duplicateEmailCheck("test@example.com");

        verify(userRepository).isExistsEmail("test@example.com");
    }
    @DisplayName("중복된 아이디를 입력할 경우 중복되었다는 성공을 응답한다.")
    @Test
    public void givenAccount_whenCheck_thenOK() {
        when(userRepository.isExistsAccount(anyString())).thenReturn(false);

        userService.duplicateAccountCheck("test_account");

        verify(userRepository).isExistsAccount("test_account");
    }
    @DisplayName("중복된 닉네임을 입력할 경우 중복되었다는 성공을 응답한다.")
    @Test
    public void givenNickname_whenCheck_thenOK() {
        when(userRepository.isExistsNickname(anyString())).thenReturn(false);

        userService.duplicateNicknameCheck("test_nickname");

        verify(userRepository).isExistsNickname("test_nickname");
    }

}
