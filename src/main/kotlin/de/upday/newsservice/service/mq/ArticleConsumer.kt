package de.upday.newsservice.service.mq

import de.upday.newsservice.model.Article
import de.upday.newsservice.service.ArticleService
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink
import reactor.core.publisher.Flux


@EnableBinding(Sink::class)
@EnableAutoConfiguration
class ArticleConsumer(val articleService: ArticleService) {

    @StreamListener
    fun receive(@Input(Sink.INPUT) input: Flux<Article>) {
        articleService.prepareAndSaveArticle(input)
    }

}