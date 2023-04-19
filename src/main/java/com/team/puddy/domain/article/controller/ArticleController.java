package com.team.puddy.domain.article.controller;

import com.team.puddy.domain.article.dto.request.RequestArticleDto;
import com.team.puddy.domain.article.dto.request.UpdateArticleDto;
import com.team.puddy.domain.article.dto.response.ResponseArticleDto;
import com.team.puddy.domain.article.dto.response.ResponseArticleListDto;
import com.team.puddy.domain.article.service.ArticleService;
import com.team.puddy.global.common.dto.Response;
import com.team.puddy.global.config.auth.JwtUserDetails;
import com.team.puddy.global.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;


    @PostMapping
    public Response<Void> addArticle(@RequestPart("request") RequestArticleDto requestDto,
                                     @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                     @AuthenticationPrincipal JwtUserDetails user) {
        articleService.addArticle(requestDto, images, user.getUserId());
        return Response.success();
    }

    @GetMapping("/{articleId}")
    public Response<ResponseArticleDto> getArticle(@PathVariable("articleId") Long articleId,
                                                   @AuthenticationPrincipal JwtUserDetails user) {
        boolean isLiked = false;
        if (user != null) {
            isLiked = articleService.checkIfLikeExists(user.getUserId(), articleId);
        }

        articleService.increaseViewCount(articleId);
        ResponseArticleDto findArticle = articleService.getArticle(articleId, isLiked);
        return Response.success(findArticle);
    }

    @GetMapping
    public Response<ResponseArticleListDto> getArticleList(@RequestParam int page,
                                                           @RequestParam(required = false, defaultValue = "") String keyword,
                                                           @RequestParam(required = false, defaultValue = "desc") String sort) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        ResponseArticleListDto articleList = articleService.getArticleListByTitleStartWith(pageable, keyword, sort);
        return Response.success(articleList);
    }

    @PutMapping("/{articleId}")
    public Response<Void> updateArticle(@PathVariable("articleId") Long articleId,
                                        @RequestPart("request") UpdateArticleDto updateDto,
                                        @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                        @AuthenticationPrincipal JwtUserDetails user) {
        articleService.updateArticle(articleId, updateDto, images, user.getUserId());
        return Response.success();
    }

    @DeleteMapping("/{articleId}")
    public Response<Void> deleteArticle(@PathVariable("articleId") Long articleId,
                                        @AuthenticationPrincipal JwtUserDetails user) {
        articleService.deleteArticle(articleId, user.getUserId());
        return Response.success();
    }

    @PatchMapping("/{articleId}/like")
    public Response<Void> like(@PathVariable("articleId") Long articleId,
                               @AuthenticationPrincipal JwtUserDetails user) {

        articleService.like(user.getUserId(), articleId);

        return Response.success();
    }

    @DeleteMapping("/{articleId}/unlike")
    public Response<Void> unLike(@PathVariable("articleId") Long articleId,
                                 @AuthenticationPrincipal JwtUserDetails user) {

        articleService.unlike(user.getUserId(), articleId);

        return Response.success();
    }

}
