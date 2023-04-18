package com.team.puddy.domain;

import com.team.puddy.domain.answer.domain.Answer;
import com.team.puddy.domain.answer.dto.RequestAnswerDto;
import com.team.puddy.domain.answer.dto.request.UpdateAnswerDto;
import com.team.puddy.domain.article.domain.Article;
import com.team.puddy.domain.article.dto.request.RequestArticleDto;
import com.team.puddy.domain.article.dto.request.UpdateArticleDto;
import com.team.puddy.domain.article.dto.response.ResponseArticleDto;
import com.team.puddy.domain.expert.domain.Expert;
import com.team.puddy.domain.expert.dto.RequestExpertDto;
import com.team.puddy.domain.expert.dto.ResponseExpertDto;
import com.team.puddy.domain.pet.domain.Pet;
import com.team.puddy.domain.pet.dto.request.RequestPetDto;
import com.team.puddy.domain.question.domain.Question;
import com.team.puddy.domain.question.dto.request.RequestQuestionDto;
import com.team.puddy.domain.question.dto.response.QuestionResponseDto;
import com.team.puddy.domain.question.dto.response.ResponseQuestionExcludeAnswerDto;
import com.team.puddy.domain.review.domain.Review;
import com.team.puddy.domain.review.dto.RequestReviewDto;
import com.team.puddy.domain.review.dto.ResponseReviewDto;
import com.team.puddy.domain.type.Category;
import com.team.puddy.domain.type.UserRole;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.dto.request.RegisterUserRequest;
import com.team.puddy.global.config.auth.JwtUserDetails;
import org.springframework.data.domain.*;

import java.util.List;

public class TestEntityUtils {


    public static User user() {
        return User.builder()
                .id(1L)
                .username("상준")
                .account("user1")
                .password("1234")
                .nickname("퍼디1234")
                .email("test@email.com")
                .role(UserRole.USER.getRole())
                .build();
    }

    public static Question question(User user) {
        return Question.builder()
                .id(13L)
                .title("title")
                .content("content")
                .answerList(List.of())
                .category(Category.짖기)
                .user(user)
                .isSolved(false)
                .postCategory(2)
                .viewCount(2L)
                .build();
    }

    public static RequestQuestionDto questionRequestDto() {
        return RequestQuestionDto.builder()
                .title("test title")
                .content("test content")
                .postCategory(2)
                .category("먹이")
                .build();
    }

    public static RegisterUserRequest createRequestDto() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .account("puddy")
                .username("username")
                .email("email@test.com")
                .isNotificated(true)
                .password("1234")
                .build();
        return request;
    }

    public static RequestExpertDto requestExpertDto() {

        return RequestExpertDto.builder().
                education("테스트대학교")
                        .introduce("저는 테스트입니다.")
                        .location("테스트시 테스트동")
                        .careerList(List.of("테스트 대학원 석사", "테스트 대학교 수의학과 학사"))
                        .build();

    }

    public static List<ResponseQuestionExcludeAnswerDto> questionList() {
        User user = user();
        Question question1 = question(user);
        Question question2 = question(user);
        Question question3 = question(user);
        Question question4 = question(user);
        Question question5 = question(user);
        List<Question> questions = List.of(question1, question2, question3, question4, question5);
        return questions.stream().map(question -> ResponseQuestionExcludeAnswerDto.builder().questionId(question.getId())
                .nickname(question.getUser().getNickname())
                .postCategory(question.getPostCategory())
                .title(question.getTitle())
                .content(question.getContent())
                .createdDate(question.getCreatedDate())
                .category(question.getCategory().name()).build()).toList();
    }

    public static Slice<ResponseQuestionExcludeAnswerDto> questionPageList() {
        List<ResponseQuestionExcludeAnswerDto> questions = questionList();
        PageRequest page = PageRequest.of(1, 10);

        return new PageImpl<>(questions, page, questions.size());
    }

    public static Expert expert() {
        return Expert.builder().id(2L)
                .education("테스트대학교")
                .introduce("저는 테스트입니다.")
                .location("테스트시 테스트동")
                .user(user())
                .careerList(List.of("테스트 대학원 석사", "테스트 대학교 수의학과 학사")).build();
    }

    public static RequestPetDto requestPetDto() {
        return RequestPetDto.builder().age(10)
                .breed("말라뮤트")
                .gender(true)
                .weight(2.8f)
                .isNeutered(true)
                .note("테스트메모")
                .name("아롱이")
                .build();
    }

    public static Pet pet() {
        return Pet.builder().age(10)
                .breed("말라뮤트")
                .gender(true)
                .weight(2.8f)
                .isNeutered(true)
                .note("테스트메모")
                .name("아롱이")
                .build();
    }

    public static QuestionResponseDto responseQuestionDto() {
        return QuestionResponseDto.builder().questionId(1L).title("22")
                .category("산책")
                .content("내용")
                .nickname("보배")
                .isSolved(true)
                .postCategory(2)
                .viewCount(2L).build();
    }

    public static Article article() {

        return Article.builder()
                .id(1L)
                .title("title")
                .content("content")
                .user(user())
                .build();

    }

    public static ResponseArticleDto responseArticleDto() {
        return ResponseArticleDto.builder()
                .articleId(1L)
                .title("title")
                .content("content")
                .nickname("nickname")
                .build();
    }

    public static UpdateArticleDto updateArticleDto() {
        return UpdateArticleDto.builder()
                .title("title")
                .content("content")
                .tagList(List.of("tag1", "tag2"))
                .build();
    }

    public static RequestArticleDto requestArticleDto() {
        return RequestArticleDto.builder()
                .title("title")
                .content("content")
                .tagList(List.of("tag1", "tag2"))
                .build();
    }

    public static JwtUserDetails jwtUserDetails() {
        return JwtUserDetails.builder()
                .id(1L)
                .isAuthenticated(true)
                .role(UserRole.USER.getRole()).build();

    }

    public static RegisterUserRequest registerUserRequest() {
        return RegisterUserRequest.builder()
                .account("account")
                .username("username")
                .email("email")
                .isNotificated(true)
                .password("password")
                .build();
    }

    public static Answer answer() {
        return Answer.builder()
                .id(1L)
                .content("content")
                .question(question(user()))
                .user(user())
                .build();
    }

    public static Review review() {
        return Review.builder()
                .id(1L)
                .content("content")
                .expert(expert())
                .build();
    }

    public static RequestReviewDto requestReviewDto() {
        return RequestReviewDto.builder()
                .content("content")
                .build();
    }

    public static ResponseReviewDto responseReviewDto() {
        return ResponseReviewDto.builder()
                .content("content")
                .build();
    }

    public static ResponseExpertDto responseExpertDto() {
        return ResponseExpertDto.builder()
                .education("education")
                .introduce("introduce")
                .location("location")
                .imagePath("")
                .careerList(List.of("career1", "career2"))
                .build();
    }

    public static RequestAnswerDto requestAnswerDto() {
        return RequestAnswerDto.builder()
                .content("content")
                .postCategory(1)
                .build();
    }

    public static UpdateAnswerDto updateAnswerDto() {
        return UpdateAnswerDto.builder()
                .content("수정 내용")
                .build();

    }
}
