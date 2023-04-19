package com.team.puddy.domain.comment.service;

import com.team.puddy.domain.TestEntityUtils;
import com.team.puddy.domain.article.domain.Article;
import com.team.puddy.domain.article.repository.ArticleRepository;
import com.team.puddy.domain.comment.domain.Comment;
import com.team.puddy.domain.comment.dto.request.RequestCommentDto;
import com.team.puddy.domain.comment.dto.request.UpdateCommentDto;
import com.team.puddy.domain.comment.repository.CommentRepository;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.repository.UserRepository;
import com.team.puddy.global.mapper.CommentMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;


@DisplayName("댓글 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UserRepository userRepository;

    @DisplayName("댓글 추가 테스트")
    @Test
    public void givenRequest_whenAddComment_thenOK() {
        Long articleId = 1L;
        Long userId = 1L;
        User user = TestEntityUtils.user();
        Article article = TestEntityUtils.article();
        Comment comment = TestEntityUtils.comment();

        RequestCommentDto requestCommentDto = TestEntityUtils.requestCommentDto();


        given(userRepository.findByUserId(userId)).willReturn(Optional.of(user));
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        given(commentMapper.toEntity(requestCommentDto, article, user)).willReturn(comment);

        commentService.addComment(requestCommentDto, articleId, userId);

        verify(userRepository, times(1)).findByUserId(userId);
        verify(articleRepository, times(1)).findById(articleId);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(commentMapper, times(1)).toEntity(requestCommentDto, article, user);
    }
    @DisplayName("댓글 삭제 테스트")
    @Test
    public void givenCommentId_whenDeleteComment_thenOK() {
        Long articleId = 1L;
        Long commentId = 1L;
        Long userId = 1L;

        given(commentRepository.existsByIdAndArticleIdAndUserId(commentId, articleId, userId)).willReturn(true);

        commentService.deleteComment(articleId, commentId, userId);

        verify(commentRepository, times(1)).existsByIdAndArticleIdAndUserId(commentId, articleId, userId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }
    @DisplayName("댓글 수정 테스트")
    @Test
    public void givenUpdate_whenUpdateComment_thenOK() {
        Long articleId = 1L;
        Long commentId = 1L;
        Long userId = 1L;
        Comment comment = TestEntityUtils.comment();
        UpdateCommentDto updateCommentDto = TestEntityUtils.updateCommentDto();
        given(commentRepository.findByIdAndArticleIdAndUserId(commentId, articleId, userId)).willReturn(Optional.of(comment));

        commentService.modifyComment(articleId, commentId, updateCommentDto, userId);

        verify(commentRepository, times(1)).findByIdAndArticleIdAndUserId(commentId, articleId, userId);
        Assertions.assertThat(comment.getContent()).isEqualTo(updateCommentDto.content());
    }
}
