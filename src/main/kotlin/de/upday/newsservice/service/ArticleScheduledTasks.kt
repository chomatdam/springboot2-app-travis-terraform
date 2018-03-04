package de.upday.newsservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import de.upday.newsservice.model.dto.ArticlesImportDto
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.core.io.ClassPathResource
import org.springframework.messaging.support.MessageBuilder
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.toMono
import java.io.InputStream

@Component
@EnableBinding(Source::class)
class ArticleScheduledTasks(val objectMapper: ObjectMapper,
                            val source: Source) {

    private val log = LoggerFactory.getLogger(ArticleScheduledTasks::class.java)

    @Scheduled(fixedRate = 15000L)
    fun importArticles() {
        // Be careful: ClassLoader.getSystemResource(url) cannot retrieve resources inside JAR archives
        ClassPathResource("articles.json").inputStream.toMono()
            .map { deserialize(it) }
            .map { randomArticle(it) }
            .subscribe(
                    { source.output().send(MessageBuilder.withPayload(it).build()) },
                    { log.error("Failed to import article: {}", it) }
            )
    }

    fun deserialize(input: InputStream): ArticlesImportDto =
            objectMapper.readValue(input, ArticlesImportDto::class.java)

    fun randomArticle(dto: ArticlesImportDto) =
            dto.articles.shuffled().first()
}