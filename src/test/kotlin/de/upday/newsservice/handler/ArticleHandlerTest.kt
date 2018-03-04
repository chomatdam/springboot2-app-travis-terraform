package de.upday.newsservice.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.nhaarman.mockito_kotlin.any
import de.upday.newsservice.model.Article
import de.upday.newsservice.model.dto.CategoriesDto
import de.upday.newsservice.repository.ArticleRepository
import de.upday.newsservice.web.routes.ArticleRoutes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import java.time.LocalDateTime


@ExtendWith(SpringExtension::class)
internal class ArticleHandlerTest {

    @MockBean
    private lateinit var articleRepository: ArticleRepository

    // WebTestClient is not using the default ObjectMapper bean
    private val mapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun init() {
        val routerFunction = ArticleRoutes().routesVersion1(ArticleHandler(articleRepository))
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()
    }

    @Test
    fun `finds all articles`() {
        `when`(articleRepository.findAll())
            .thenReturn(Flux.fromIterable(articles))

        webTestClient.get()
            .uri("/api/v1/articles")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .consumeWith {
                val articles: List<Article> = mapper.readValue(it.responseBody!!)
                assertThat(articles).isNotEmpty
                assertThat(articles).contains(*articles.toTypedArray())
            }
    }

    @Test
    fun `finds articles for a specific category`() {
        `when`(articleRepository.findArticleByCategories(any()))
            .thenReturn(Flux.just(article3, article4))

        webTestClient.post()
            .uri("/api/v1/articles/categories")
            .syncBody(CategoriesDto(listOf("cat2")))
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .consumeWith {
                val articles: List<Article> = mapper.readValue(it.responseBody!!)
                assertThat(articles).hasSize(2)
                assertThat(articles).contains(article3, article4)
            }
    }

    companion object {
        val article1 = Article(id = 1,
                               title = "title",
                               url = "http://url.com",
                               imageUrl = "http://imageUrl.com",
                               description = "description",
                               categories = emptyList(),
                               createdAt = LocalDateTime.now(),
                               updatedAt = null,
                               source = "source"
        )
        val article2 = article1.copy(id = 2,
                                     updatedAt = LocalDateTime.now(),
                                     categories = listOf("cat1")
        )
        val article3 = article1.copy(id = 3,
                                     updatedAt = LocalDateTime.now(),
                                     categories = listOf("cat1", "cat2")
        )
        val article4 = article1.copy(id = 4,
                                     categories = listOf("cat2", "cat3")
        )
        val articles = listOf(article1,
                              article2,
                              article3,
                              article4)
    }
}