package de.upday.newsservice.web.routes

import de.upday.newsservice.handler.ArticleHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.server.router

@Configuration
@EnableWebFlux
class ArticleRoutes {

    @Bean
    fun routesVersion1(articleHandler: ArticleHandler) = router {
        "/api/v1".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("/articles", articleHandler::articles)
                POST("/articles/categories", articleHandler::articlesByCategories)
            }
        }
    }

}