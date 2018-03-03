package de.upday.newsservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import de.upday.newsservice.model.dto.ArticlesImportDto
import de.upday.newsservice.repository.ArticleRepository
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Tag
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.toMono

@Component
class ArticleImporterTask(val objectMapper: ObjectMapper,
                          val articleRepository: ArticleRepository) {

    private val log = LoggerFactory.getLogger(ArticleImporterTask::class.java)

    @Scheduled(fixedRate = 15000L)
    fun importArticles() {
        // Be careful: ClassLoader.getSystemResource(url) cannot retrieve resources inside JAR archives
        ClassPathResource("articles.json").inputStream.toMono()
            .map { objectMapper.readValue<ArticlesImportDto>(it, ArticlesImportDto::class.java) }
            .flatMap { articleRepository.save(it.articles.shuffled().first()) }
            .subscribe(
                    {
                        log.debug("Article saved: {}", it)
                        Metrics.counter("import.articles", listOf(Tag.of("status", "success")))
                    },
                    {
                        log.error("Failed to save article: {}", it)
                        Metrics.counter("import.articles", listOf(Tag.of("status", "failed")))
                    }
            )
    }
}