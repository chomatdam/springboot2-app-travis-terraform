package de.upday.newsservice.repository

import de.upday.newsservice.model.Article
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface ArticleRepository: ReactiveMongoRepository<Article, Long> {

    @Query("{'categories' : { \$in: ?0} }")
    fun findArticleByCategories(categories: Flux<String>): Flux<Article>

}
