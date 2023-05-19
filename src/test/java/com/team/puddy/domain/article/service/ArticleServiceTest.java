package com.team.puddy.domain.article.service;

import com.team.puddy.domain.TestEntityUtils;
import com.team.puddy.domain.article.domain.Article;
import com.team.puddy.domain.article.domain.Tag;
import com.team.puddy.domain.article.dto.request.RequestArticleDto;
import com.team.puddy.domain.article.dto.request.UpdateArticleDto;
import com.team.puddy.domain.article.dto.response.ResponseArticleDto;
import com.team.puddy.domain.article.repository.ArticleRepository;
import com.team.puddy.domain.article.repository.ArticleTagRepository;
import com.team.puddy.domain.article.repository.LikeRepository;
import com.team.puddy.domain.article.repository.TagRepository;
import com.team.puddy.domain.image.domain.Image;
import com.team.puddy.domain.image.service.ImageService;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.repository.UserRepository;
import com.team.puddy.global.error.exception.NotFoundException;
import com.team.puddy.global.mapper.ArticleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("게시글 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleService articleService;

    @Mock
    private UserRepository userQueryRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    ArticleTagRepository articleTagRepository;

    @Mock
    private TagRepository tagRepository;
    @Mock
    private LikeRepository likeRepository;

    private final Long userId = 1L;
    private User user;
    private RequestArticleDto requestDto;
    private List<MultipartFile> images;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .build();

        requestDto = RequestArticleDto.builder()
                .title("Test title")
                .content("Test content")
                .tagList(Arrays.asList("tag1", "tag2"))
                .build();

        images = Arrays.asList(
                new MockMultipartFile("image1", new byte[0]),
                new MockMultipartFile("image2", new byte[0])
        );
    }
    @DisplayName("필수 정보로 게시글 작성시 성공한다.")
    @Test
    void givenRequest_whenAddArticle_thenOK() {
        // Given
        Article article = TestEntityUtils.article();
        when(userQueryRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(imageService.saveImageListToArticle(images)).thenReturn(Arrays.asList(new Image(), new Image()));
        when(articleMapper.toEntity(any(RequestArticleDto.class), anyList(), any(User.class))).thenReturn(article);
        when(tagRepository.findByTagName(anyString())).thenReturn(Optional.of(new Tag("tag1")));

        // When
        articleService.addArticle(requestDto, images, userId);

        // Then
        verify(userQueryRepository, times(1)).findByUserId(userId);
        verify(imageService, times(1)).saveImageListToArticle(images);
        verify(articleMapper, times(1)).toEntity(any(RequestArticleDto.class), anyList(), any(User.class));
        verify(tagRepository, times(requestDto.tagList().size())).findByTagName(anyString());
        verify(articleRepository, times(1)).save(any(Article.class));
    }
    @DisplayName("유효한 게시글 id로 게시글 조회시 성공한다.")
    @Test
    void givenArticleId_whenGetArticle_thenOK() {
        // Given
        Long articleId = 1L;
        boolean isLike = true;
        Article article = TestEntityUtils.article();
        ResponseArticleDto responseArticleDto = TestEntityUtils.responseArticleDto();

        when(articleRepository.findArticleWithUserById(articleId)).thenReturn(Optional.of(article));
        when(likeRepository.countByArticleId(articleId)).thenReturn(1L);
        when(articleMapper.toDto(anyLong(), anyBoolean(), any(Article.class), anyList())).thenReturn(responseArticleDto);

        // When
        ResponseArticleDto result = articleService.getArticle(articleId, true);
        // Then
        assertNotNull(responseArticleDto);
        verify(articleRepository, times(1)).findArticleWithUserById(articleId);
        verify(likeRepository, times(1)).countByArticleId(articleId);
        verify(articleMapper, times(1)).toDto(anyLong(), anyBoolean(), any(Article.class), anyList());
    }
    @DisplayName("유효하지 않은 게시글 id로 게시글 조회시 예외가 발생한다.")
    @Test
    void givenArticleId_whenGetArticle_then400() {
        // Given
        Long articleId = 1L;
        boolean isLike = true;

        when(articleRepository.findArticleWithUserById(articleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> articleService.getArticle(articleId, isLike));

        verify(articleRepository, times(1)).findArticleWithUserById(articleId);
    }

    @DisplayName("필수 정보로 게시글 수정시 성공한다.")
    @Test
    void givenUpdate_whenUpdateArticle_thenOK() {
        // Given
        Long articleId = 1L;
        UpdateArticleDto updateDto = TestEntityUtils.updateArticleDto();

        Article article = mock(Article.class);
        when(articleRepository.findArticleForModify(articleId, userId)).thenReturn(Optional.of(article));

        // When
        articleService.updateArticle(articleId, updateDto, images, userId);

        // Then
        verify(articleRepository, times(1)).findArticleForModify(articleId, userId);
        verify(imageService, times(1)).updateImageListForArticle(article, images);
        verify(articleTagRepository, times(1)).deleteByArticleId(articleId);
        verify(article, times(1)).updateArticle(eq(updateDto.title()), eq(updateDto.content()), anyList());
    }
    @DisplayName("유효한 게시글 id로 삭제시 성공한다")
    @Test
    void givenArticleId_whenDeleteArticle_thenOK() {
        // given
        Long articleId = 1L;
        Long userId = 1L;

        Article article = mock(Article.class);
        when(articleRepository.findArticleForModify(articleId, userId)).thenReturn(Optional.of(article));

        // when
        articleService.deleteArticle(articleId, userId);

        // then
        verify(articleRepository, times(1)).findArticleForModify(articleId, userId);
        verify(imageService, times(1)).deleteImageListFromQuestion(article.getImageList());
        verify(articleTagRepository, times(1)).deleteByArticleId(articleId);
        verify(articleRepository, times(1)).deleteById(articleId);
    }

}
