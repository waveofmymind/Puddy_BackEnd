package com.team.puddy.domain.comment.service;

import com.team.puddy.domain.article.domain.Article;
import com.team.puddy.domain.article.repository.ArticleRepository;
import com.team.puddy.domain.comment.domain.Comment;
import com.team.puddy.domain.comment.dto.request.RequestCommentDto;
import com.team.puddy.domain.comment.dto.request.UpdateCommentDto;
import com.team.puddy.domain.comment.repository.CommentRepository;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.repository.UserRepository;
import com.team.puddy.global.error.ErrorCode;
import com.team.puddy.global.error.exception.NotFoundException;
import com.team.puddy.global.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    @Transactional
    public void addComment(RequestCommentDto request, Long articleId, Long userId) {
        User findUser = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        Article findArticle = articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        Comment comment = commentMapper.toEntity(request, findArticle, findUser);
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long articleId, Long commentId, Long userId) {
        if(commentRepository.existsByIdAndArticleIdAndUserId(commentId, articleId, userId)) {
            commentRepository.deleteById(commentId);
        } else {
            throw new NotFoundException(ErrorCode.COMMENT_DELETE_ERROR);
        }

    }

    @Transactional
    public void modifyComment(Long articleId, Long commentId, UpdateCommentDto request, Long userId) {
        Comment findComment = commentRepository.findByIdAndArticleIdAndUserId(commentId, articleId, userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MODIFIY_COMMENT_ERROR));

        findComment.update(request.content());

    }
}
