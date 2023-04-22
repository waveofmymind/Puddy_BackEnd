package com.team.puddy.global.mapper;

import com.team.puddy.domain.article.domain.Article;
import com.team.puddy.domain.article.dto.request.RequestArticleDto;
import com.team.puddy.domain.article.dto.response.ResponseArticleDto;
import com.team.puddy.domain.article.dto.response.ResponseArticleExcludeCommentDto;
import com.team.puddy.domain.article.dto.response.ResponseArticleListDto;
import com.team.puddy.domain.comment.dto.response.ResponseCommentDto;
import com.team.puddy.domain.image.domain.Image;
import com.team.puddy.domain.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ArticleMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "user",source = "user")
    @Mapping(target = "tagList", ignore = true)
    @Mapping(target = "imageList",source = "imageList")
    @Mapping(target = "commentList",ignore = true)
    @Mapping(target = "viewCount",ignore = true)
    @Mapping(target = "likeList",ignore = true)
    Article toEntity(RequestArticleDto request, List<Image> imageList, User user);

    default ResponseArticleDto toDto(Long likeCount,boolean isLike,Article article, List<ResponseCommentDto> commentDtoList) {
        return ResponseArticleDto.builder()
                .likeCount(likeCount)
                .isLike(isLike)
                .articleId(article.getId())
                .createdDate(article.getCreatedDate())
                .nickname(article.getUser().getNickname())
                .title(article.getTitle())
                .content(article.getContent())
                .images(article.getImageList().stream().map(Image::getImagePath).toList())
                .tagList(article.getTagList())
                .commentList(commentDtoList)
                .viewCount(article.getViewCount())
                .postCategory(article.getPostCategory())
                .build();
    }

    default ResponseArticleExcludeCommentDto toDto(Article article,String imagePath,long likeCount) {
        return ResponseArticleExcludeCommentDto.builder()
                .articleId(article.getId())
                .title(article.getTitle())
                .nickname(article.getUser().getNickname())
                .content(article.getContent())
                .viewCount(article.getViewCount())
                .likeCount(likeCount)
                .imagePath(imagePath)
                .createdDate(article.getCreatedDate())
                .tagList(article.getTagList())
                .postCategory(article.getPostCategory()).build();
    }

    default ResponseArticleExcludeCommentDto toExcludeImageDto(Article article) {
        return ResponseArticleExcludeCommentDto.builder()
                .articleId(article.getId())
                .title(article.getTitle())
                .nickname(article.getUser().getNickname())
                .content(article.getContent())
                .viewCount(article.getViewCount())
                .likeCount(0)
                .imagePath(null)
                .createdDate(article.getCreatedDate())
                .tagList(article.getTagList())
                .postCategory(article.getPostCategory()).build();
    }
    default ResponseArticleListDto toDto (List<ResponseArticleExcludeCommentDto> articleList, boolean hasNextPage) {
        return ResponseArticleListDto.builder()
                .articleList(articleList)
                .hasNextPage(hasNextPage).build();
    }
}
