package de.upday.newsservice.handler

import de.upday.newsservice.model.Article
import de.upday.newsservice.model.dto.CategoriesDto
import de.upday.newsservice.repository.ArticleRepository
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToFlux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@Component
class ArticleHandler(private val articleRepository: ArticleRepository) {

    fun articles(req: ServerRequest): Mono<ServerResponse> =
            ok().contentType(APPLICATION_JSON)
                .body(articleRepository.findAll(), Article::class.java)
                .switchIfEmpty(notFound().build())

    fun articlesByCategories(req: ServerRequest): Mono<ServerResponse> =
            articleRepository.findArticleByCategories(req.bodyToFlux<CategoriesDto>().flatMapIterable { it.categories })
                .transform {
                    ok().contentType(APPLICATION_JSON)
                        .body(it, Article::class.java)
                        .switchIfEmpty(notFound().build())
                }
                .toMono()

}

