package de.upday.newsservice.service

import de.upday.newsservice.model.Article
import de.upday.newsservice.repository.ArticleRepository
import de.upday.newsservice.service.mq.ArticleConsumer
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Tag
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.LocalDateTime

@Service
class ArticleService(val articleRepository: ArticleRepository) {

    private val log = LoggerFactory.getLogger(ArticleConsumer::class.java)


    fun prepareAndSaveArticle(articleFlux: Flux<Article>) {
        articleFlux
            .flatMap {
                articleFlux.zipWith(articleRepository.existsById(it.id))
            }
            .map {
                var article = it.t1
                if (it.t2) {
                    article = article.copy(updatedAt = LocalDateTime.now())
                }
                article
            }
            .flatMap { articleRepository.save(it) }
            .subscribe(
                    {
                        log.info("Article saved: {}", it)
                    },
                    {
                        Metrics.counter("import.articles", listOf(Tag.of("status", "failed")))
                        log.error("Failed to save article: {}", it)
                    }
            )
    }
}