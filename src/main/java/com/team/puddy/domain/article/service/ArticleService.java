package com.team.puddy.domain.article.service;

import com.team.puddy.domain.article.domain.Article;
import com.team.puddy.domain.article.domain.ArticleTag;
import com.team.puddy.domain.article.domain.Likes;
import com.team.puddy.domain.article.domain.Tag;
import com.team.puddy.domain.article.dto.request.RequestArticleDto;
import com.team.puddy.domain.article.dto.request.UpdateArticleDto;
import com.team.puddy.domain.article.dto.response.ResponseArticleDto;
import com.team.puddy.domain.article.dto.response.ResponseArticleExcludeCommentDto;
import com.team.puddy.domain.article.dto.response.ResponseArticleListDto;
import com.team.puddy.domain.article.repository.ArticleRepository;
import com.team.puddy.domain.article.repository.ArticleTagRepository;
import com.team.puddy.domain.article.repository.LikeRepository;
import com.team.puddy.domain.article.repository.TagRepository;
import com.team.puddy.domain.image.domain.Image;
import com.team.puddy.domain.image.service.ImageService;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.repository.UserQueryRepository;
import com.team.puddy.domain.user.repository.UserRepository;
import com.team.puddy.global.error.ErrorCode;
import com.team.puddy.global.error.exception.NotFoundException;
import com.team.puddy.global.mapper.ArticleMapper;
import com.team.puddy.global.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    private final ImageService imageService;
    private final TagRepository tagRepository;
    private final ArticleTagRepository articleTagRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public void addArticle(RequestArticleDto requestDto, List<MultipartFile> images, Long userId) {
        //작성 유저 찾기
        User findUser = userRepository.findByUserId(userId).orElseThrow(
                () -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        //이미지 저장하기
        List<Image> imageList = imageService.saveImageListToArticle(images);
        Article article = articleMapper.toEntity(requestDto, imageList, findUser);
        //태그 매핑하기
        List<ArticleTag> articleTagList = requestDto.tagList().stream().map(tagName -> tagRepository.findByTagName(tagName)
                .map(tag -> new ArticleTag(article, tag)).orElseGet(
                        () -> new ArticleTag(article, new Tag(tagName))
                )).toList();

        article.setTagList(articleTagList);

        articleRepository.save(article);


    }

    @Transactional(readOnly = true)
    public ResponseArticleDto getArticle(Long articleId, boolean isLike) {
        Article findArticle = articleRepository.findArticleWithUserById(articleId).orElseThrow(
                () -> new NotFoundException(ErrorCode.ARTICLE_NOT_FOUND)
        );
        long likeCount = likeRepository.countByArticleId(articleId);
        return articleMapper.toDto(likeCount, isLike, findArticle, findArticle.getCommentList().stream().map(comment ->
                commentMapper.toDto(comment, comment.getUser().getNickname())).toList());
    }

    @Transactional
    public void updateArticle(Long articleId, UpdateArticleDto updateDto, List<MultipartFile> images, Long userId) {
        Article findArticle = articleRepository.findArticleForModify(articleId, userId).orElseThrow(
                () -> new NotFoundException(ErrorCode.UNAUTHORIZED_OPERATION)
        );
        //기존 이미지가 있을 경우 제거한다.
        imageService.updateImageListForArticle(findArticle, images);
        //기존 게시글의 태그를 찾아 지운다.
        articleTagRepository.deleteByArticleId(articleId);
        //새로 요청받은 태그 처리하기
        List<ArticleTag> articleTagList = updateDto.tagList().stream().map(tagName -> tagRepository.findByTagName(tagName)
                .map(tag -> new ArticleTag(findArticle, tag)).orElseGet(
                        () -> new ArticleTag(findArticle, new Tag(tagName))
                )).toList();

        findArticle.updateArticle(updateDto.title(), updateDto.content(), articleTagList);

    }

    @Transactional(readOnly = true)
    public List<ResponseArticleExcludeCommentDto> getPopularArticleList() {
        List<Article> popularArticleList = articleRepository.findPopularArticleList();
        return popularArticleList.stream().map(article -> articleMapper.toDto(article, "",0L)).toList();
    }

    @Transactional(readOnly = true)
    public List<ResponseArticleExcludeCommentDto> getRecentArticleList() {
        List<Article> recentArticleList = articleRepository.findRecentArticleList();
        return recentArticleList.stream().map(article -> articleMapper.toDto(article, "",0L)).toList();
    }

    @Transactional(readOnly = true)
    public ResponseArticleListDto getArticleListByTitleStartWith(Pageable pageable, String keyword) {
        Slice<Article> articleList = articleRepository.findAllByTitleStartingWithOrderByModifiedDateDesc(keyword, pageable);
        List<ResponseArticleExcludeCommentDto> articleDtos = articleList.stream().map(article -> {
            Long likeCount = likeRepository.countByArticleId(article.getId());
            String imagePath = getFirstImagePath(article.getImageList());

            return articleMapper.toDto(article, imagePath, likeCount);
        }).toList();
        return articleMapper.toDto(articleDtos, articleList.hasNext());
    }

    @Transactional
    public void increaseViewCount(Long articleId) {
        if (!articleRepository.existsById(articleId)) {
            throw new NotFoundException(ErrorCode.ARTICLE_NOT_FOUND);
        }
        articleRepository.increaseViewCount(articleId);
    }

    @Transactional
    public String getFirstImagePath(List<Image> imageList) {
        return Optional.ofNullable(imageList)
                .filter(images -> !images.isEmpty())
                .map(images -> images.get(0).getImagePath())
                .orElse("");
    }

    @Transactional
    public void like(Long userId, Long articleId) {
        Article findArticle = articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException(ErrorCode.ARTICLE_NOT_FOUND));

        findArticle.addLikes(userId);
    }

    @Transactional
    public void deleteArticle(Long articleId, Long userId) {
        Article findArticle = articleRepository.findArticleForModify(articleId, userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.UNAUTHORIZED_OPERATION));
        //S3에 저장된 이미지를 지운다.
        List<Image> findImages = findArticle.getImageList();
        imageService.deleteImageListFromQuestion(findImages);
        //태그도 지운다.
        articleTagRepository.deleteByArticleId(articleId);
        //질문글을 지운다.
        articleRepository.deleteById(articleId);
    }

    @Transactional
    public void unlike(Long userId, Long articleId) {
        Article findArticle = articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException(ErrorCode.ARTICLE_NOT_FOUND));

        Likes like = findArticle.removeLikes(userId);

        if (like != null) {
            likeRepository.delete(like);
        }
    }

    public boolean checkIfLikeExists(long userId, long articleId) {
        return likeRepository.findByUserIdAndArticleId(userId, articleId).isPresent();
    }
}
