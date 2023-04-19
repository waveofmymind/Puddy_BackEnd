package com.team.puddy.domain.article.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.puddy.domain.article.domain.Article;
import com.team.puddy.domain.question.domain.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import static com.team.puddy.domain.article.domain.QArticle.article;
import static com.team.puddy.domain.article.domain.QArticleTag.articleTag;
import static com.team.puddy.domain.comment.domain.QComment.comment;
import static com.team.puddy.domain.expert.domain.QExpert.expert;
import static com.team.puddy.domain.image.domain.QImage.image;
import static com.team.puddy.domain.pet.domain.QPet.pet;
import static com.team.puddy.domain.question.domain.QQuestion.question;
import static com.team.puddy.domain.user.domain.QUser.user;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArticleQueryRepositoryImpl implements ArticleQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Article> findArticleWithUserById(Long articleId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(article)
                        .leftJoin(article.user, user).fetchJoin()
                        .leftJoin(user.pet,pet).fetchJoin()
                        .leftJoin(user.expert,expert).fetchJoin()
                        .leftJoin(article.commentList,comment).fetchJoin()
                        .where(article.id.eq(articleId))
                        .fetchOne());
    }


    public Slice<Article> findAllByTitleStartingWithOrderByCreatedDateDesc(String keyword, Pageable pageable) {
            List<Article> articleList = queryFactory.selectFrom(article).distinct()
                    .leftJoin(article.user,user).fetchJoin()
                    .leftJoin(user.expert,expert).fetchJoin()
                    .where(article.title.startsWith(keyword))
                    .orderBy(article.createdDate.desc())
                    .limit(pageable.getPageSize() + 1)
                    .offset(pageable.getOffset())
                    .fetch();

            boolean hasNext = articleList.size() > pageable.getPageSize();
            if (hasNext) {
                articleList.remove(articleList.size()-1);
            }
            return new SliceImpl<>(articleList,pageable,hasNext);
    }

    public Slice<Article> findAllByTitleStartingWithOrderByCreatedDateAsc(String keyword, Pageable pageable) {
        List<Article> articleList = queryFactory.selectFrom(article).distinct()
                .leftJoin(article.user,user).fetchJoin()
                .leftJoin(user.expert,expert).fetchJoin()
                .where(article.title.startsWith(keyword))
                .orderBy(article.createdDate.asc())
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();

        boolean hasNext = articleList.size() > pageable.getPageSize();
        if (hasNext) {
            articleList.remove(articleList.size()-1);
        }
        return new SliceImpl<>(articleList,pageable,hasNext);
    }

    public Slice<Article> findAllByTitleStartingWithOrderByViewCountDesc(String keyword, Pageable pageable) {
        List<Article> articleList = queryFactory.selectFrom(article).distinct()
                .leftJoin(article.user,user).fetchJoin()
                .leftJoin(user.expert,expert).fetchJoin()
                .where(article.title.startsWith(keyword))
                .orderBy(article.viewCount.desc())
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();

        boolean hasNext = articleList.size() > pageable.getPageSize();
        if (hasNext) {
            articleList.remove(articleList.size()-1);
        }
        return new SliceImpl<>(articleList,pageable,hasNext);
    }

    public List<Article> findPopularArticleList() {
        return queryFactory.selectFrom(article).distinct()
                .leftJoin(article.user, user).fetchJoin()
                .orderBy(article.viewCount.desc())
                .limit(5)
                .fetch();
    }
    public List<Article> findRecentArticleList() {
        return queryFactory.selectFrom(article)
                .distinct()
                .leftJoin(article.user,user).fetchJoin()
                .orderBy(article.modifiedDate.desc())
                .limit(5)
                .fetch();
    }

    public Optional<Article> findArticleForModify(Long articleId,Long userId) {
        return Optional.ofNullable(queryFactory.selectFrom(article)
                .leftJoin(article.imageList, image).fetchJoin()
                .where(article.user.id.eq(userId).and(article.id.eq(articleId)))
                .fetchOne());
    }

}
